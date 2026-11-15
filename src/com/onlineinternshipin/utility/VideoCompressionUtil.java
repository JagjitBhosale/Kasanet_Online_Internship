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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * VideoCompressionUtil — High-Speed Server-Side Video Compression Utility using FFmpeg.
 *
 * Optimizations:
 * 1. ultrafast H.264 preset with multithreading (-threads 0) for sub-5s compression.
 * 2. Intelligent resolution downscaling with even padding to prevent odd-dimension failures.
 * 3. 30 fps cap (-r 30) to eliminate bloated 60/120 fps overhead on lectures.
 * 4. Safe audio stream mapping (-map 0:v:0 -map 0:a?) so videos without audio never fail.
 * 5. Web streaming optimization (+faststart) and universal browser compatibility (yuv420p).
 * 6. Dynamic FFmpeg discovery across WinGet, system PATH, environment variables, and Linux production paths.
 * 7. Bitrate-cap mode for large files to GUARANTEE output fits under Cloudinary limit.
 */
public class VideoCompressionUtil {

    private static final Logger logger = LoggerFactory.getLogger(VideoCompressionUtil.class);

    /**
     * Threshold in bytes above which server-side compression is applied (default: 15 MB).
     * Smaller files bypass compression for instant uploads.
     */
    public static final long COMPRESSION_THRESHOLD_BYTES = 15L * 1024L * 1024L;

    /**
     * Cloudinary free-plan hard limit (100 MB). Compressed output must stay below this.
     */
    public static final long CLOUDINARY_MAX_BYTES = 95L * 1024L * 1024L; // 95 MB safety margin

    /**
     * Maximum execution time for video compression process (15 minutes for large/long files).
     */
    private static final long COMPRESSION_TIMEOUT_MINUTES = 15L;

    // Cache the resolved FFmpeg path — reset on failure so next call retries
    private static volatile String cachedFfmpegPath = null;
    private static volatile Boolean ffmpegAvailable = null;

    private VideoCompressionUtil() {}

    /**
     * Compresses the given video file if its size exceeds the threshold.
     *
     * Strategy:
     * - Files > 95 MB: Use bitrate-cap mode with ultrafast preset @ 480p.
     *   Calculates exact bitrate from video duration to guarantee output < 85 MB.
     * - Files between 15-95 MB: Use CRF 28 with ultrafast preset @ 720p.
     *
     * @param inputFile The temporary video file uploaded by the admin
     * @return A compressed File if successful, or the original inputFile if skipped/failed.
     * @throws IOException if FFmpeg is unavailable AND file exceeds Cloudinary limit
     */
    public static File compressVideo(File inputFile) throws IOException {
        if (inputFile == null || !inputFile.exists()) {
            return inputFile;
        }

        long originalBytes = inputFile.length();
        double originalMb = originalBytes / (1024.0 * 1024.0);

        if (originalBytes < COMPRESSION_THRESHOLD_BYTES) {
            logger.info("Skipping video compression: file size ({} MB) is below threshold ({} MB)",
                    String.format(Locale.US, "%.2f", originalMb),
                    String.format(Locale.US, "%.2f", COMPRESSION_THRESHOLD_BYTES / (1024.0 * 1024.0)));
            return inputFile;
        }

        String ffmpegPath = getFfmpegExecutablePath();
        if (ffmpegPath == null) {
            if (originalBytes > CLOUDINARY_MAX_BYTES) {
                throw new IOException(
                    "FFmpeg is not installed on this server. Cannot compress the video (" +
                    String.format(Locale.US, "%.0f", originalMb) + " MB). " +
                    "Cloudinary has a 100 MB upload limit. " +
                    "Please install FFmpeg on the server, or upload a video smaller than 100 MB.");
            }
            logger.warn("FFmpeg not detected. File is {} MB (under 95 MB limit), uploading as-is.",
                    String.format(Locale.US, "%.2f", originalMb));
            return inputFile;
        }

        logger.info("Starting server-side compression on {} ({} MB) using FFmpeg at: {}",
                inputFile.getName(), String.format(Locale.US, "%.2f", originalMb), ffmpegPath);

        // ── STRATEGY: Choose compression based on file size ──
        if (originalBytes > CLOUDINARY_MAX_BYTES) {
            // ── LARGE FILE (> 95 MB): Bitrate-cap mode ──
            // Use ffprobe to get duration, then calculate exact bitrate to guarantee < 85 MB output
            double durationSec = getVideoDuration(ffmpegPath, inputFile);
            if (durationSec <= 0) {
                durationSec = 3600; // fallback: assume 1 hour
                logger.warn("Could not determine video duration, assuming 3600s");
            }

            // Target 85 MB output: total bitrate = (85 * 8 * 1024 kbits) / duration_seconds
            // Subtract audio bitrate to get video-only target
            int audioBitrateKbps = 48; // 48 kbps for speech (adequate for lectures)
            int totalTargetKbps = (int) ((85.0 * 8 * 1024) / durationSec);
            int targetBitrateKbps = totalTargetKbps - audioBitrateKbps;
            if (targetBitrateKbps < 80) targetBitrateKbps = 80; // minimum video quality floor

            // For very long videos (>2 hours), use even more aggressive settings
            if (durationSec > 7200) {
                audioBitrateKbps = 32;
                targetBitrateKbps = totalTargetKbps - audioBitrateKbps;
                if (targetBitrateKbps < 64) targetBitrateKbps = 64;
            }

            logger.info("Large file mode: {} MB, duration={} sec, video bitrate={} kbps, audio={} kbps @ 480p ultrafast",
                    String.format(Locale.US, "%.1f", originalMb),
                    String.format(Locale.US, "%.0f", durationSec),
                    targetBitrateKbps, audioBitrateKbps);

            // Single pass: ultrafast preset, 480p, bitrate-capped
            String scaleFilter = "scale='min(854,iw)':-2:flags=bicubic,pad=ceil(iw/2)*2:ceil(ih/2)*2";
            File compressed = runFfmpeg(ffmpegPath, inputFile, -1, scaleFilter, "ultrafast", targetBitrateKbps, audioBitrateKbps);

            if (compressed == null) {
                throw new IOException(
                    "FFmpeg compression failed for " + String.format(Locale.US, "%.0f", originalMb) +
                    " MB video. Cloudinary limit is 100 MB. Please try a shorter or smaller video.");
            }

            double compressedMb = compressed.length() / (1024.0 * 1024.0);

            // Final size check
            if (compressed.length() > CLOUDINARY_MAX_BYTES) {
                logger.warn("Compressed output is still {} MB (over 95 MB limit), deleting.", 
                        String.format(Locale.US, "%.1f", compressedMb));
                compressed.delete();
                throw new IOException(
                    "Video is still " + String.format(Locale.US, "%.0f", compressedMb) +
                    " MB after compression. Cloudinary limit is 100 MB. " +
                    "Please upload a shorter or lower-quality video.");
            }

            double reduction = (1.0 - ((double) compressed.length() / originalBytes)) * 100.0;
            logger.info("Compression complete: {} MB → {} MB ({}% reduction) — under Cloudinary limit ✓",
                    String.format(Locale.US, "%.1f", originalMb),
                    String.format(Locale.US, "%.1f", compressedMb),
                    String.format(Locale.US, "%.1f", reduction));
            return compressed;

        } else {
            // ── NORMAL FILE (15-95 MB): CRF mode ──
            // Quality-based compression at 720p — fast and good quality
            logger.info("Normal file mode: {} MB, CRF 28 @ 720p ultrafast",
                    String.format(Locale.US, "%.1f", originalMb));

            String scaleFilter = "scale='min(1280,iw)':-2:flags=bicubic,pad=ceil(iw/2)*2:ceil(ih/2)*2";
            File compressed = runFfmpeg(ffmpegPath, inputFile, 28, scaleFilter, "ultrafast", -1, 96);

            if (compressed == null) {
                logger.warn("FFmpeg compression failed, returning original file.");
                return inputFile;
            }

            double compressedMb = compressed.length() / (1024.0 * 1024.0);
            double reduction = (1.0 - ((double) compressed.length() / originalBytes)) * 100.0;
            logger.info("Compression complete: {} MB → {} MB ({}% reduction)",
                    String.format(Locale.US, "%.1f", originalMb),
                    String.format(Locale.US, "%.1f", compressedMb),
                    String.format(Locale.US, "%.1f", reduction));
            return compressed;
        }
    }

    /**
     * Runs a single FFmpeg compression pass with the given CRF/bitrate and scale filter.
     *
     * @param targetBitrateKbps if > 0, uses bitrate cap mode instead of CRF
     * @param audioBitrateKbps audio bitrate in kbps (48 for compressed, 96 for normal)
     * @return Compressed file, or null if FFmpeg failed.
     */
    private static File runFfmpeg(String ffmpegPath, File inputFile, int crf, String scaleFilter,
                                   String preset, int targetBitrateKbps, int audioBitrateKbps) {
        File compressedFile = null;
        try {
            compressedFile = File.createTempFile("kasnet_compressed_", ".mp4");

            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-y");
            command.add("-i");
            command.add(inputFile.getAbsolutePath());
            command.add("-vf");
            command.add(scaleFilter);
            command.add("-r");
            command.add("30");
            command.add("-c:v");
            command.add("libx264");
            command.add("-preset");
            command.add(preset);
            if (targetBitrateKbps > 0) {
                // Bitrate-cap mode: guarantees output fits within size limit
                command.add("-b:v");
                command.add(targetBitrateKbps + "k");
                command.add("-maxrate");
                command.add(targetBitrateKbps + "k");
                command.add("-bufsize");
                command.add((targetBitrateKbps * 2) + "k");
            } else {
                // CRF mode: constant quality
                command.add("-crf");
                command.add(String.valueOf(crf));
            }
            command.add("-threads");
            command.add("0");
            command.add("-pix_fmt");
            command.add("yuv420p");
            command.add("-map");
            command.add("0:v:0");
            command.add("-map");
            command.add("0:a?");
            command.add("-c:a");
            command.add("aac");
            command.add("-b:a");
            command.add(audioBitrateKbps + "k");
            command.add("-movflags");
            command.add("+faststart");
            command.add("-max_muxing_queue_size");
            command.add("2048");
            command.add(compressedFile.getAbsolutePath());

            logger.info("FFmpeg command: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            long startTime = System.currentTimeMillis();
            Process process = pb.start();

            StringBuilder processOutput = new StringBuilder();
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
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
                logger.error("FFmpeg timed out after {} minutes. Killing process.", COMPRESSION_TIMEOUT_MINUTES);
                process.destroyForcibly();
                if (compressedFile.exists()) compressedFile.delete();
                return null;
            }

            int exitCode = process.exitValue();
            long durationMs = System.currentTimeMillis() - startTime;

            if (exitCode != 0 || !compressedFile.exists() || compressedFile.length() == 0) {
                logger.error("FFmpeg exited with code {}. Output:\n{}", exitCode, processOutput);
                if (compressedFile.exists()) compressedFile.delete();
                return null;
            }

            double inputMb = inputFile.length() / (1024.0 * 1024.0);
            double outputMb = compressedFile.length() / (1024.0 * 1024.0);
            logger.info("FFmpeg pass completed in {}s: {} MB -> {} MB",
                    String.format(Locale.US, "%.1f", durationMs / 1000.0),
                    String.format(Locale.US, "%.2f", inputMb),
                    String.format(Locale.US, "%.2f", outputMb));

            return compressedFile;

        } catch (Exception e) {
            logger.error("Exception during FFmpeg compression pass.", e);
            if (compressedFile != null && compressedFile.exists()) {
                compressedFile.delete();
            }
            return null;
        }
    }

    /**
     * Resolves the path to the FFmpeg executable.
     * Re-checks every time if previous check failed (no negative caching).
     */
    public static synchronized String getFfmpegExecutablePath() {
        if (cachedFfmpegPath != null) {
            // Verify cached path still works
            File f = new File(cachedFfmpegPath);
            if (f.exists() || "ffmpeg".equals(cachedFfmpegPath)) {
                return cachedFfmpegPath;
            }
            // Cached path no longer valid, re-discover
            cachedFfmpegPath = null;
            ffmpegAvailable = null;
        }

        logger.info("Searching for FFmpeg executable...");

        // 1. Check EnvUtil / System Property / Environment Variable for FFMPEG_PATH
        String envPath = null;
        try {
            envPath = EnvUtil.get("FFMPEG_PATH");
            logger.info("EnvUtil.get(FFMPEG_PATH) = {}", envPath);
        } catch (Exception e) {
            logger.warn("EnvUtil.get failed: {}", e.getMessage());
        }
        if (envPath == null || envPath.trim().isEmpty()) {
            envPath = System.getProperty("ffmpeg.path");
        }
        if (envPath == null || envPath.trim().isEmpty()) {
            envPath = System.getenv("FFMPEG_PATH");
        }
        if (envPath != null && !envPath.trim().isEmpty()) {
            File f = new File(envPath.trim());
            if (f.exists() && f.isFile()) {
                if (checkFfmpegExecutable(f.getAbsolutePath())) {
                    cachedFfmpegPath = f.getAbsolutePath();
                    ffmpegAvailable = true;
                    logger.info("FFmpeg found via FFMPEG_PATH: {}", cachedFfmpegPath);
                    return cachedFfmpegPath;
                } else {
                    logger.warn("FFMPEG_PATH file exists but failed execution check: {}", f.getAbsolutePath());
                }
            } else {
                logger.warn("FFMPEG_PATH points to non-existent file: {}", envPath);
            }
        }

        // 2. Check if "ffmpeg" is in PATH
        if (checkFfmpegExecutable("ffmpeg")) {
            cachedFfmpegPath = "ffmpeg";
            ffmpegAvailable = true;
            logger.info("FFmpeg found in system PATH");
            return cachedFfmpegPath;
        }

        // 3. Dynamic search in WinGet Packages directory
        String localAppData = System.getenv("LOCALAPPDATA");
        String userHome = System.getProperty("user.home", "");
        if (localAppData == null || localAppData.trim().isEmpty()) {
            localAppData = userHome + File.separator + "AppData" + File.separator + "Local";
        }
        logger.info("Searching WinGet packages at LOCALAPPDATA: {}", localAppData);

        File wingetDir = new File(localAppData, "Microsoft" + File.separator + "WinGet" + File.separator + "Packages");
        if (wingetDir.exists() && wingetDir.isDirectory()) {
            File discovered = findFfmpegRecursive(wingetDir, 5);
            if (discovered != null) {
                if (checkFfmpegExecutable(discovered.getAbsolutePath())) {
                    cachedFfmpegPath = discovered.getAbsolutePath();
                    ffmpegAvailable = true;
                    logger.info("FFmpeg found via WinGet search: {}", cachedFfmpegPath);
                    return cachedFfmpegPath;
                } else {
                    logger.warn("WinGet ffmpeg.exe found but failed execution check: {}", discovered.getAbsolutePath());
                }
            }
        }

        // 4. Check WinGet Links directory
        File wingetLinks = new File(localAppData, "Microsoft" + File.separator + "WinGet" + File.separator + "Links" + File.separator + "ffmpeg.exe");
        if (wingetLinks.exists() && checkFfmpegExecutable(wingetLinks.getAbsolutePath())) {
            cachedFfmpegPath = wingetLinks.getAbsolutePath();
            ffmpegAvailable = true;
            logger.info("FFmpeg found via WinGet Links: {}", cachedFfmpegPath);
            return cachedFfmpegPath;
        }

        // 5. Known common candidate paths (Windows, Linux, macOS)
        String[] candidatePaths = {
            "C:\\Program Files\\ffmpeg\\bin\\ffmpeg.exe",
            "C:\\ffmpeg\\bin\\ffmpeg.exe",
            "C:\\ProgramData\\chocolatey\\bin\\ffmpeg.exe",
            // Linux / Docker production environments
            "/usr/bin/ffmpeg",
            "/usr/local/bin/ffmpeg",
            "/opt/homebrew/bin/ffmpeg"
        };

        for (String candidate : candidatePaths) {
            File f = new File(candidate);
            if (f.exists() && f.isFile()) {
                if (checkFfmpegExecutable(candidate)) {
                    cachedFfmpegPath = candidate;
                    ffmpegAvailable = true;
                    logger.info("FFmpeg found at known path: {}", cachedFfmpegPath);
                    return cachedFfmpegPath;
                }
            }
        }

        // NOT FOUND — but do NOT cache the failure so next call retries
        ffmpegAvailable = false;
        logger.warn("FFmpeg executable not detected on server. Searched: FFMPEG_PATH env, system PATH, " +
                     "WinGet packages at {}, and known paths.", localAppData);
        return null;
    }

    /**
     * Recursively searches for an ffmpeg.exe binary within a directory up to maxDepth.
     */
    private static File findFfmpegRecursive(File dir, int maxDepth) {
        if (dir == null || !dir.exists() || maxDepth < 0) return null;
        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File f : files) {
            if (f.isFile() && "ffmpeg.exe".equalsIgnoreCase(f.getName())) {
                return f;
            }
        }
        for (File f : files) {
            if (f.isDirectory()) {
                File found = findFfmpegRecursive(f, maxDepth - 1);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Checks if the given FFmpeg command or executable path is working.
     */
    private static boolean checkFfmpegExecutable(String path) {
        try {
            ProcessBuilder pb = new ProcessBuilder(path, "-version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            // Consume output to avoid blocking
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                while (reader.readLine() != null) { /* drain */ }
            }
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                return true;
            }
            process.destroyForcibly();
        } catch (Exception e) {
            logger.debug("FFmpeg check failed for '{}': {}", path, e.getMessage());
        }
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

    /**
     * Gets the duration of a video file in seconds using ffprobe (bundled with ffmpeg).
     */
    private static double getVideoDuration(String ffmpegPath, File videoFile) {
        try {
            // Derive ffprobe path from ffmpeg path
            String ffprobePath;
            if ("ffmpeg".equals(ffmpegPath)) {
                ffprobePath = "ffprobe";
            } else {
                ffprobePath = ffmpegPath.replace("ffmpeg.exe", "ffprobe.exe")
                                         .replace("ffmpeg", "ffprobe");
            }
            // If ffprobe doesn't exist at derived path, try just "ffprobe"
            if (!"ffprobe".equals(ffprobePath)) {
                File ffprobeFile = new File(ffprobePath);
                if (!ffprobeFile.exists()) {
                    ffprobePath = "ffprobe";
                }
            }

            logger.info("Getting video duration with: {} for {}", ffprobePath, videoFile.getName());

            ProcessBuilder pb = new ProcessBuilder(
                ffprobePath, "-v", "quiet", "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1", videoFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line.trim());
                }
            }

            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0 && output.length() > 0) {
                double duration = Double.parseDouble(output.toString().trim());
                logger.info("Video duration: {} seconds", String.format(Locale.US, "%.1f", duration));
                return duration;
            }
            process.destroyForcibly();
        } catch (Exception e) {
            logger.warn("Could not determine video duration: {}", e.getMessage());
        }
        return -1;
    }
}
