package com.kasnet.util;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

/**
 * FileUtil - File Upload & Management Utility
 *
 * Provides helpers for validating, naming, and deleting uploaded files
 * in the Kasnet Online Internship Portal.
 *
 * Usage:
 *   String safeName = FileUtil.generateSafeFileName("My Resume (2024).pdf");
 *   boolean ok      = FileUtil.isAllowedResumeType("resume.pdf");
 */
public class FileUtil {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Allowed image file extensions (profile photos, banners, etc.). */
    public static final String[] ALLOWED_IMAGE_TYPES  = {"jpg", "jpeg", "png"};

    /** Allowed resume/CV file extensions. */
    public static final String[] ALLOWED_RESUME_TYPES = {"pdf", "doc", "docx"};

    /**
     * Maximum permitted upload size: 5 MB.
     * Expressed in bytes: 5 * 1024 * 1024 = 5,242,880 bytes.
     */
    public static final long MAX_FILE_SIZE = 5L * 1024L * 1024L; // 5 MB

    // Private constructor — utility class
    private FileUtil() {
        throw new UnsupportedOperationException("FileUtil is a utility class.");
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Generates a collision-resistant, filesystem-safe file name by combining a
     * random UUID with the original file's extension.
     *
     * <p>The original name is discarded to prevent path-traversal and special-
     * character injection attacks.</p>
     *
     * <p>Example: {@code "My Résumé (final).PDF"} → {@code "a3f1c2d4-...-uuid.pdf"}</p>
     *
     * @param originalName the file name as received from the client (may contain
     *                     spaces, special chars, mixed case, etc.)
     * @return a safe, unique file name with the original extension (lower-cased)
     * @throws IllegalArgumentException if originalName is null or empty
     */
    public static String generateSafeFileName(String originalName) {
        if (originalName == null || originalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Original file name must not be null or empty.");
        }
        String ext = getFileExtension(originalName);
        String uuid = UUID.randomUUID().toString(); // e.g. "550e8400-e29b-41d4-a716-446655440000"
        return ext.isEmpty() ? uuid : uuid + "." + ext.toLowerCase();
    }

    /**
     * Extracts the file extension from a file name (without the leading dot).
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code "resume.PDF"}   → {@code "pdf"}</li>
     *   <li>{@code "photo.tar.gz"} → {@code "gz"}</li>
     *   <li>{@code "noextension"}  → {@code ""}</li>
     * </ul>
     * </p>
     *
     * @param fileName the file name to inspect (may include path separators)
     * @return the lower-cased extension string without a dot, or {@code ""} if none
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        // Strip any directory prefix first (handles both / and \)
        String baseName = new File(fileName).getName();
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == baseName.length() - 1) {
            return ""; // no dot, or dot is the last character
        }
        return baseName.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * Checks whether the file's extension is one of {@link #ALLOWED_IMAGE_TYPES}.
     *
     * @param fileName the uploaded file name
     * @return {@code true} if the extension is jpg, jpeg, or png (case-insensitive)
     */
    public static boolean isAllowedImageType(String fileName) {
        String ext = getFileExtension(fileName);
        return Arrays.asList(ALLOWED_IMAGE_TYPES).contains(ext);
    }

    /**
     * Checks whether the file's extension is one of {@link #ALLOWED_RESUME_TYPES}.
     *
     * @param fileName the uploaded file name
     * @return {@code true} if the extension is pdf, doc, or docx (case-insensitive)
     */
    public static boolean isAllowedResumeType(String fileName) {
        String ext = getFileExtension(fileName);
        return Arrays.asList(ALLOWED_RESUME_TYPES).contains(ext);
    }

    /**
     * Checks whether the given file size (in bytes) is within {@link #MAX_FILE_SIZE}.
     *
     * @param sizeBytes the file size in bytes (as reported by the multipart request)
     * @return {@code true} if {@code sizeBytes} is between 1 and {@value #MAX_FILE_SIZE} bytes
     */
    public static boolean isFileSizeAllowed(long sizeBytes) {
        return sizeBytes > 0 && sizeBytes <= MAX_FILE_SIZE;
    }

    /**
     * Deletes the file at the given absolute or relative path.
     *
     * <p>This method returns {@code false} (rather than throwing) if:
     * <ul>
     *   <li>The path is null or empty</li>
     *   <li>The file does not exist</li>
     *   <li>The path points to a directory</li>
     *   <li>Deletion fails (e.g. permission denied)</li>
     * </ul>
     * </p>
     *
     * @param filePath the filesystem path of the file to delete
     * @return {@code true} if the file existed and was successfully deleted
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        File file = new File(filePath.trim());
        if (!file.exists()) {
            System.err.println("[FileUtil] deleteFile: file not found — " + filePath);
            return false;
        }
        if (file.isDirectory()) {
            System.err.println("[FileUtil] deleteFile: path is a directory — " + filePath);
            return false;
        }
        boolean deleted = file.delete();
        if (!deleted) {
            System.err.println("[FileUtil] deleteFile: failed to delete — " + filePath);
        }
        return deleted;
    }
}
