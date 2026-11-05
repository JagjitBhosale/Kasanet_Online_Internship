package com.onlineinternshipin.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * VideoCompressionUtil — Server-Side Video Compression Utility using FFmpeg.
 *
 * Compresses large video uploads (100MB+) down to ~15-25MB using H.264 (CRF 28)
 * and AAC audio, optimized for fast web streaming (+faststart).
 *
 * If FFmpeg is not found or fails, it gracefully falls back to the original file
 * so uploads never fail.
 */
public class VideoCompressionUtil {

    private static final Logger logger = LoggerFactory.getLogger(VideoCompressionUtil.class);

    /**
     * Threshold in bytes above which server-side compression is applied (default: 15 MB).
     * Smaller files are passed through without modification.
     */
    public static final long COMPRESSION_THRESHOLD_BYTES = 15L * 1024L * 1024L;

    /**
     * Maximum execution time for video compression process (10 minutes).
     */
    private static final long COMPRESSION_TIMEOUT_MINUTES = 10L;

    // Cache the resolved FFmpeg path
    private static volatile String cachedFfmpegPath = null;
    private static volatile Boolean ffmpegAvailable = null;

    private VideoCompressionUtil() {}

    /**
     * Compresses the given video file if its size exceeds {@value #COMPRESSION_THRESHOLD_BYTES}.
     *
     * @param inputFile The temporary video file uploaded by the admin
     * @return A compressed File if successful, or the original inputFile if skipped/failed.
     */
    public static File compressVideo(File inputFile) {
        if (inputFile == null || !inputFile.exists()) {
            return inputFile;
        }

        long originalBytes = inputFile.length();
        double originalMb = originalBytes / (1024.0 * 1024.0);

        if (originalBytes < COMPRESSION_THRESHOLD_BYTES) {
            logger.info("Skipping video compression: file size ({:.2f} MB) is below threshold ({:.2f} MB)",
                    originalMb, (COMPRESSION_THRESHOLD_BYTES / (1024.0 * 1024.0)));
            return inputFile;
        }

        String ffmpegPath = getFfmpegExecutablePath();
        if (ffmpegPath == null) {
            logger.warn("FFmpeg executable not detected on server. Skipping compression for {:.2f} MB video.", originalMb);
            return inputFile;
        }

        logger.info("Starting server-side compression on {} ({:.2f} MB) using FFmpeg at: {}",
                inputFile.getName(), originalMb, ffmpegPath);

        File compressedFile = null;
        try {
            compressedFile = File.createTempFile("kasnet_compressed_", ".mp4");

            // Build FFmpeg command:
            // -y : overwrite output
            // -i <input>
            // -c:v libx264 : H.264 video codec
            // -crf 28 : visually lossless / optimized compression for lectures (lower = higher quality, 28 = ~80% size cut)
            // -preset veryfast : high encoding speed to minimize server CPU time
            // -c:a aac -b:a 128k : compressed AAC audio
            // -movflags +faststart : relocate moov atom to front for immediate web playback
            // -max_muxing_queue_size 1024 : prevent buffer overflows on high bitrate inputs
            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-y");
            command.add("-i");
            command.add(inputFile.getAbsolutePath());
            command.add("-c:v");
            command.add("libx264");
            command.add("-crf");
            command.add("28");
            command.add("-preset");
            command.add("veryfast");
            command.add("-c:a");
            command.add("aac");
            command.add("-b:a");
            command.add("128k");
            command.add("-movflags");
            command.add("+faststart");
            command.add("-max_muxing_queue_size");
            command.add("1024");
            command.add(compressedFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // combine stdout and stderr

            long startTime = System.currentTimeMillis();
            Process process = pb.start();

            // Consume process output in background to prevent buffer deadlocks
            StringBuilder processOutput = new StringBuilder();
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Keep only the last 30 lines of FFmpeg output
                        if (processOutput.length() > 4096) {
                            processOutput.delete(0, 2048);
                        }
                        processOutput.append(line).append("\n");
                    }
                } catch (IOException ignored) {}
            });
            outputReader.start();

            boolean finished = process.waitFor(COMPRESSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            outputReader.join(2000);

            if (!finished) {
                logger.error("Video compression timed out after {} minutes. Killing FFmpeg process.", COMPRESSION_TIMEOUT_MINUTES);
                process.destroyForcibly();
                if (compressedFile.exists()) compressedFile.delete();
                return inputFile;
            }

            int exitCode = process.exitValue();
            long durationMs = System.currentTimeMillis() - startTime;

            if (exitCode != 0 || !compressedFile.exists() || compressedFile.length() == 0) {
                logger.error("FFmpeg exited with error code {}. Output:\n{}", exitCode, processOutput);
                if (compressedFile != null && compressedFile.exists()) {
                    compressedFile.delete();
                }
                return inputFile;
            }

            long compressedBytes = compressedFile.length();
            double compressedMb = compressedBytes / (1024.0 * 1024.0);

            // If compressed file is somehow larger than the original, stick with original
            if (compressedBytes >= originalBytes) {
                logger.info("Compressed file ({:.2f} MB) is not smaller than original ({:.2f} MB). Keeping original.",
                        compressedMb, originalMb);
                compressedFile.delete();
                return inputFile;
            }

            double reductionPercent = (1.0 - ((double) compressedBytes / originalBytes)) * 100.0;
            logger.info("Successfully compressed video in {:.1f}s: {:.2f} MB -> {:.2f} MB ({:.1f}% reduction)",
                    (durationMs / 1000.0), originalMb, compressedMb, reductionPercent);

            return compressedFile;

        } catch (Exception e) {
            logger.error("Exception during video compression. Falling back to original file.", e);
            if (compressedFile != null && compressedFile.exists()) {
                compressedFile.delete();
            }
            return inputFile;
        }
    }

    /**
     * Resolves the path to the FFmpeg executable.
     */
    public static synchronized String getFfmpegExecutablePath() {
        if (cachedFfmpegPath != null) {
            return cachedFfmpegPath;
        }

        // 1. Check System Property or Environment Variable
        String envPath = System.getProperty("ffmpeg.path");
        if (envPath == null || envPath.trim().isEmpty()) {
            envPath = System.getenv("FFMPEG_PATH");
        }
        if (envPath != null && new File(envPath).canExecute()) {
            cachedFfmpegPath = envPath;
            ffmpegAvailable = true;
            return cachedFfmpegPath;
        }

        // 2. Check if "ffmpeg" is in PATH
        if (checkFfmpegExecutable("ffmpeg")) {
            cachedFfmpegPath = "ffmpeg";
            ffmpegAvailable = true;
            return cachedFfmpegPath;
        }

        // 3. Known Windows locations (WinGet, AppData, Program Files)
        String userHome = System.getProperty("user.home", "");
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null || localAppData.trim().isEmpty()) {
            localAppData = userHome + File.separator + "AppData" + File.separator + "Local";
        }

        String[] candidatePaths = {
            // WinGet Gyan FFmpeg build
            localAppData + "\\Microsoft\\WinGet\\Packages\\Gyan.FFmpeg_Microsoft.Winget.Source_8wekyb3d8bbwe\\ffmpeg-8.0.1-full_build\\bin\\ffmpeg.exe",
            // General WinGet paths
            localAppData + "\\Microsoft\\WinGet\\Links\\ffmpeg.exe",
            // Program Files
            "C:\\Program Files\\ffmpeg\\bin\\ffmpeg.exe",
            "C:\\ffmpeg\\bin\\ffmpeg.exe",
            // CapCut installed FFmpeg
            localAppData + "\\CapCut\\Apps\\7.5.0.3053\\ffmpeg.exe",
            localAppData + "\\CapCut\\Apps\\7.1.0.2881\\ffmpeg.exe",
            localAppData + "\\CapCut\\Apps\\6.6.0.2616\\ffmpeg.exe"
        };

        for (String candidate : candidatePaths) {
            File f = new File(candidate);
            if (f.exists() && f.isFile()) {
                if (checkFfmpegExecutable(candidate)) {
                    cachedFfmpegPath = candidate;
                    ffmpegAvailable = true;
                    return cachedFfmpegPath;
                }
            }
        }

        ffmpegAvailable = false;
        return null;
    }

    /**
     * Checks if the given FFmpeg command or executable path is working.
     */
    private static boolean checkFfmpegExecutable(String path) {
        try {
            Process process = new ProcessBuilder(path, "-version").redirectErrorStream(true).start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                return true;
            }
            process.destroyForcibly();
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Returns true if FFmpeg is available on the host system.
     */
    public static boolean isFfmpegAvailable() {
        if (ffmpegAvailable == null) {
            getFfmpegExecutablePath();
        }
        return Boolean.TRUE.equals(ffmpegAvailable);
    }
}
