package com.kasnet.service;

import com.kasnet.util.FileUtil;
import com.kasnet.util.ValidationUtil;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service class for handling file upload operations.
 * Manages resume saving with type/size validation, safe filename generation,
 * and cleanup of old files.
 */
public class FileUploadService {

    // Maximum allowed resume file size: 5 MB
    private static final long MAX_RESUME_SIZE_BYTES = 5 * 1024 * 1024L;

    // Allowed MIME types for resume uploads
    private static final List<String> ALLOWED_RESUME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    // Allowed file extensions for resume uploads
    private static final List<String> ALLOWED_RESUME_EXTENSIONS = Arrays.asList(
            ".pdf", ".doc", ".docx"
    );

    // Subdirectory under the upload root for resumes
    private static final String RESUME_SUBDIR = "resumes";

    /**
     * Validates, saves a resume file to disk, and returns its relative path.
     *
     * @param filePart      The {@link Part} object from the multipart request
     * @param uploadDirPath Absolute path to the root upload directory
     * @param userId        The ID of the user uploading the file (used in filename)
     * @return Relative path to the saved file (e.g., "resumes/user_42_abc123.pdf")
     * @throws IOException  if file I/O fails
     * @throws Exception    if validation fails (type, size, or missing file)
     */
    public String saveResume(Part filePart, String uploadDirPath, int userId)
            throws IOException, Exception {

        // --- Null checks ---
        if (filePart == null) {
            throw new IllegalArgumentException("No file was submitted. Please select a resume to upload.");
        }
        if (uploadDirPath == null || uploadDirPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Upload directory path is not configured.");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID for file upload.");
        }

        // --- Extract original filename ---
        String originalFileName = getContentDispositionFilename(filePart.getHeader("content-disposition"));
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Could not determine the uploaded file name.");
        }
        originalFileName = originalFileName.trim();

        // --- Validate file extension ---
        String extension = FileUtil.getFileExtension(originalFileName).toLowerCase();
        if (!ALLOWED_RESUME_EXTENSIONS.contains(extension)) {
            throw new Exception("Invalid file type. Only PDF, DOC, and DOCX files are allowed for resumes.");
        }

        // --- Validate MIME type ---
        String contentType = filePart.getContentType();
        if (contentType == null || !ALLOWED_RESUME_TYPES.contains(contentType.toLowerCase())) {
            throw new Exception("Invalid file format detected. Please upload a valid PDF or Word document.");
        }

        // --- Validate file size ---
        long fileSize = filePart.getSize();
        if (fileSize <= 0) {
            throw new IllegalArgumentException("The uploaded file appears to be empty. Please select a valid resume.");
        }
        if (fileSize > MAX_RESUME_SIZE_BYTES) {
            throw new Exception("Resume file size exceeds the maximum allowed limit of 5 MB.");
        }

        // --- Build the target directory ---
        String resumeDir = uploadDirPath + File.separator + RESUME_SUBDIR;
        File resumeDirFile = new File(resumeDir);
        if (!resumeDirFile.exists()) {
            boolean created = resumeDirFile.mkdirs();
            if (!created) {
                throw new IOException("Failed to create resume upload directory: " + resumeDir);
            }
        }

        // --- Generate a safe, unique filename ---
        String safeFilename = generateSafeFilename(userId, extension);
        String absoluteFilePath = resumeDir + File.separator + safeFilename;

        // --- Write the file to disk ---
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, Paths.get(absoluteFilePath), StandardCopyOption.REPLACE_EXISTING);
        }

        // --- Return the relative path for storage in DB ---
        return RESUME_SUBDIR + "/" + safeFilename;
    }

    /**
     * Deletes an old resume file from the filesystem if it exists.
     *
     * @param oldRelativePath The relative path of the old file (as stored in DB),
     *                        e.g., "resumes/user_42_abc123.pdf"
     *                        Pass the base upload dir as prefix via FileUtil or resolve externally.
     */
    public void deleteOldResume(String oldPath) {
        if (oldPath == null || oldPath.trim().isEmpty()) {
            return;
        }
        File oldFile = new File(oldPath.trim());
        if (oldFile.exists() && oldFile.isFile()) {
            boolean deleted = oldFile.delete();
            if (!deleted) {
                System.err.println("[FileUploadService] Warning: Could not delete old resume file: " + oldPath);
            }
        }
    }

    /**
     * Extracts the original filename from a Content-Disposition header value.
     * Handles both {@code filename} and {@code filename*} (RFC 5987) parameters,
     * with fallback to plain parsing.
     *
     * Example header: {@code form-data; name="resume"; filename="my resume.pdf"}
     *
     * @param header The value of the Content-Disposition header
     * @return The extracted filename, or {@code null} if not found
     */
    public String getContentDispositionFilename(String header) {
        if (header == null || header.trim().isEmpty()) {
            return null;
        }

        String filename = null;

        // Split on semicolon to get individual parameters
        String[] parts = header.split(";");
        for (String part : parts) {
            String trimmed = part.trim();

            // Handle standard filename= parameter
            if (trimmed.toLowerCase().startsWith("filename=")) {
                filename = trimmed.substring("filename=".length()).trim();
                // Remove surrounding quotes if present
                if (filename.startsWith("\"") && filename.endsWith("\"")) {
                    filename = filename.substring(1, filename.length() - 1);
                }
                // Don't break here; prefer filename* over filename if both present
            }

            // Handle RFC 5987 filename*= parameter (takes precedence)
            if (trimmed.toLowerCase().startsWith("filename*=")) {
                String rfc5987Value = trimmed.substring("filename*=".length()).trim();
                // Format: charset'language'encoded-value
                if (rfc5987Value.contains("'")) {
                    String[] rfc5987Parts = rfc5987Value.split("'", 3);
                    if (rfc5987Parts.length == 3) {
                        try {
                            String charset = rfc5987Parts[0];
                            String encodedName = rfc5987Parts[2];
                            filename = java.net.URLDecoder.decode(encodedName,
                                    charset.isEmpty() ? "UTF-8" : charset);
                        } catch (Exception e) {
                            // Fall through to use the already-parsed filename
                        }
                    }
                }
                // filename* takes highest priority — return immediately
                if (filename != null && !filename.isEmpty()) {
                    return sanitizeFilename(filename);
                }
            }
        }

        return filename != null ? sanitizeFilename(filename) : null;
    }

    // -------------------------------------------------------------------------
    // Private helper methods
    // -------------------------------------------------------------------------

    /**
     * Generates a safe, unique filename incorporating the userId and a UUID segment.
     * Pattern: {@code user_<userId>_<uuid-short><extension>}
     *
     * @param userId    The uploading user's ID
     * @param extension The file extension (including leading dot, e.g., ".pdf")
     * @return A safe unique filename string
     */
    private String generateSafeFilename(int userId, String extension) {
        String uniquePart = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "user_" + userId + "_" + uniquePart + extension;
    }

    /**
     * Sanitizes a filename by removing path traversal characters and
     * replacing unsafe characters with underscores.
     *
     * @param filename The raw filename to sanitize
     * @return A safe filename string
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) return null;
        // Remove any path components (prevent directory traversal)
        String name = new File(filename).getName();
        // Replace characters that are unsafe in filenames
        name = name.replaceAll("[^a-zA-Z0-9._\\-]", "_");
        return name;
    }
}
