package com.kasnet.util;

import com.kasnet.service.FileUploadService;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AssignmentFileStorage {

    private static final Logger LOGGER = Logger.getLogger(AssignmentFileStorage.class.getName());
    private static final String RELATIVE_DIRECTORY = "assignment-instructions";
    private static final long MAX_PDF_SIZE = 5L * 1024L * 1024L;

    private AssignmentFileStorage() {
        throw new UnsupportedOperationException("AssignmentFileStorage is a utility class.");
    }

    public static String storePdf(ServletContext context, Part part) throws IOException {
        String submittedName = new FileUploadService().getContentDispositionFilename(
                part.getHeader("content-disposition"));
        validatePdfName(submittedName);
        if (part.getSize() <= 0 || part.getSize() > MAX_PDF_SIZE) {
            throw new IllegalArgumentException("The instruction PDF must be between 1 byte and 5 MB.");
        }
        String contentType = part.getContentType();
        String mediaType = contentType == null ? ""
                : contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        if (!"application/pdf".equals(mediaType)
                && !"application/x-pdf".equals(mediaType)
                && !"application/octet-stream".equals(mediaType)) {
            throw new IllegalArgumentException("The instruction file must have a PDF content type.");
        }
        try (InputStream input = part.getInputStream()) {
            byte[] header = input.readNBytes(5);
            if (header.length != 5 || header[0] != '%'
                    || header[1] != 'P' || header[2] != 'D' || header[3] != 'F' || header[4] != '-') {
                throw new IllegalArgumentException("The uploaded file is not a valid PDF document.");
            }
        }

        Path directory = getStorageDirectory(context);
        Files.createDirectories(directory);
        String fileName = UUID.randomUUID().toString() + ".pdf";
        Path destination = directory.resolve(fileName).normalize();
        if (!destination.getParent().equals(directory) || Files.exists(destination)) {
            throw new IOException("Unable to allocate a unique instruction file.");
        }

        Path temporary = Files.createTempFile(directory, "instruction-", ".tmp");
        try {
            try (InputStream input = part.getInputStream()) {
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(temporary, destination);
        } catch (IOException | RuntimeException exception) {
            Files.deleteIfExists(temporary);
            throw exception;
        }
        return RELATIVE_DIRECTORY + "/" + fileName;
    }

    public static Path resolveStoredPdf(ServletContext context, String relativePath) throws IOException {
        if (relativePath == null || !relativePath.matches(
                "assignment-instructions/[0-9a-fA-F-]{36}\\.pdf")) {
            throw new IOException("Invalid stored instruction path.");
        }
        Path root = getStorageDirectory(context).toRealPath();
        Path file = root.resolve(relativePath.substring(RELATIVE_DIRECTORY.length() + 1)).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            throw new IOException("Instruction document is unavailable.");
        }
        Path realFile = file.toRealPath();
        if (!realFile.startsWith(root)) {
            throw new IOException("Invalid instruction document location.");
        }
        return realFile;
    }

    public static void deleteStoredPdf(ServletContext context, String relativePath) {
        if (relativePath == null || !relativePath.matches(
                "assignment-instructions/[0-9a-fA-F-]{36}\\.pdf")) {
            return;
        }
        try {
            Path root = getStorageDirectory(context).toAbsolutePath().normalize();
            Path file = root.resolve(relativePath.substring(RELATIVE_DIRECTORY.length() + 1)).normalize();
            if (file.startsWith(root)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Could not remove replaced assignment instruction PDF.", exception);
        }
    }

    private static Path getStorageDirectory(ServletContext context) {
        String configuredPath = context.getInitParameter("assignmentInstructionsUploadDir");
        if (configuredPath == null || configuredPath.trim().isEmpty()) {
            configuredPath = "uploads";
        }
        Path configured = Path.of(configuredPath.trim());
        if (!configured.isAbsolute()) {
            String catalinaBase = System.getProperty("catalina.base", System.getProperty("user.home"));
            configured = Path.of(catalinaBase).resolve(configured);
        }
        return configured.toAbsolutePath().normalize().resolve(RELATIVE_DIRECTORY).normalize();
    }

    private static void validatePdfName(String submittedName) {
        if (submittedName == null || submittedName.trim().isEmpty() || submittedName.length() > 255
                || submittedName.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("Select a PDF instruction file with a valid filename.");
        }
        String baseName = submittedName.replace('\\', '/');
        baseName = baseName.substring(baseName.lastIndexOf('/') + 1);
        int extensionStart = baseName.lastIndexOf('.');
        String extension = extensionStart < 0 ? "" : baseName.substring(extensionStart + 1);
        if (!"pdf".equals(extension.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Only PDF instruction documents are allowed.");
        }
    }
}
