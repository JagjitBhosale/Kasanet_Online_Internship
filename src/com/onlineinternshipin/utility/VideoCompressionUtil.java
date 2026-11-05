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
 * 2. Intelligent resolution downscaling to max 720p (-vf scale='min(1280,iw)':-2) with even padding
 *    to prevent odd-dimension failures and cut pixel processing by up to 85%.
 * 3. 30 fps cap (-r 30) to eliminate bloated 60/120 fps overhead on lectures.
 * 4. Safe audio stream mapping (-map 0:v:0 -map 0:a?) so videos without audio never fail.
 * 5. Web streaming optimization (+faststart) and universal browser compatibility (yuv420p).
 * 6. Dynamic FFmpeg discovery across WinGet, system PATH, environment variables, and Linux production paths.
 * 7. Non-blocking graceful fallback to original file if FFmpeg is unavailable or times out.
 */
public class VideoCompressionUtil {

    private static final Logger logger = LoggerFactory.getLogger(VideoCompressionUtil.class);

    /**
     * Threshold in bytes above which server-side compression is applied (default: 15 MB).
     * Smaller files bypass compression for instant uploads.
     */
    public static final long COMPRESSION_THRESHOLD_BYTES = 15L * 1024L * 1024L;

    /**
     * Maximum execution time for video compression process (3 minutes).
     */
    private static final long COMPRESSION_TIMEOUT_MINUTES = 3L;

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
            logger.info("Skipping video compression: file size ({} MB) is below threshold ({} MB)",
                    String.format(Locale.US, "%.2f", originalMb),
                    String.format(Locale.US, "%.2f", COMPRESSION_THRESHOLD_BYTES / (1024.0 * 1024.0)));
            return inputFile;
        }

        String ffmpegPath = getFfmpegExecutablePath();
        if (ffmpegPath == null) {
            logger.warn("FFmpeg executable not detected on server. Skipping compression for {} MB video.",
                    String.format(Locale.US, "%.2f", originalMb));
            return inputFile;
        }

        logger.info("Starting fast server-side compression on {} ({} MB) using FFmpeg at: {}",
                inputFile.getName(), String.format(Locale.US, "%.2f", originalMb), ffmpegPath);

        File compressedFile = null;
        try {
            compressedFile = File.createTempFile("kasnet_compressed_", ".mp4");

            // Build optimized high-speed FFmpeg command:
            // -y : overwrite destination
            // -i <input>
            // -vf scale='min(1280,iw)':-2:flags=bicubic,pad=ceil(iw/2)*2:ceil(ih/2)*2 :
            //      downscale >720p to 720p, keep <=720p as is, guarantee even dimensions for H.264
            // -r 30 : cap framerate at 30fps for lectures
            // -c:v libx264 : standard H.264 video codec
            // -preset ultrafast : maximize encoding speed to avoid HTTP request timeouts
            // -crf 28 : optimal size-to-quality ratio for lectures / slides / screen captures
            // -threads 0 : utilize all CPU cores
            // -pix_fmt yuv420p : broad browser/mobile HTML5 video compatibility
            // -map 0:v:0 -map 0:a? : map video and optional audio (never fail on mute videos)
            // -c:a aac -b:a 96k -ar 44100 : clean, compressed voice audio
            // -movflags +faststart : relocate moov atom to front for instant web playback
            // -max_muxing_queue_size 2048 : prevent buffer overflows on high bitrate streams
            List<String> command = new ArrayList<>();
            command.add(ffmpegPath);
            command.add("-y");
            command.add("-i");
            command.add(inputFile.getAbsolutePath());
            command.add("-vf");
            command.add("scale='min(1280,iw)':-2:flags=bicubic,pad=ceil(iw/2)*2:ceil(ih/2)*2");
            command.add("-r");
            command.add("30");
            command.add("-c:v");
            command.add("libx264");
            command.add("-preset");
            command.add("ultrafast");
            command.add("-crf");
            command.add("28");
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
            command.add("96k");
            command.add("-ar");
            command.add("44100");
            command.add("-movflags");
            command.add("+faststart");
            command.add("-max_muxing_queue_size");
            command.add("2048");
            command.add(compressedFile.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            long startTime = System.currentTimeMillis();
            Process process = pb.start();

            // Consume process output in background thread to prevent stdout/stderr buffer deadlocks
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

            // If compressed file is not smaller than original, stick with original
            if (compressedBytes >= originalBytes) {
                logger.info("Compressed file ({} MB) is not smaller than original ({} MB). Keeping original.",
                        String.format(Locale.US, "%.2f", compressedMb),
                        String.format(Locale.US, "%.2f", originalMb));
                compressedFile.delete();
                return inputFile;
            }

            double reductionPercent = (1.0 - ((double) compressedBytes / originalBytes)) * 100.0;
            logger.info("Successfully compressed video in {}s: {} MB -> {} MB ({}% reduction)",
                    String.format(Locale.US, "%.1f", durationMs / 1000.0),
                    String.format(Locale.US, "%.2f", originalMb),
                    String.format(Locale.US, "%.2f", compressedMb),
                    String.format(Locale.US, "%.1f", reductionPercent));

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

        // 1. Check EnvUtil / System Property / Environment Variable
        String envPath = EnvUtil.get("FFMPEG_PATH");
        if (envPath == null || envPath.trim().isEmpty()) {
            envPath = System.getProperty("ffmpeg.path");
        }
        if (envPath == null || envPath.trim().isEmpty()) {
            envPath = System.getenv("FFMPEG_PATH");
        }
        if (envPath != null && !envPath.trim().isEmpty()) {
            File f = new File(envPath.trim());
            if (f.exists() && checkFfmpegExecutable(f.getAbsolutePath())) {
                cachedFfmpegPath = f.getAbsolutePath();
                ffmpegAvailable = true;
                return cachedFfmpegPath;
            }
        }

        // 2. Check if "ffmpeg" is in PATH
        if (checkFfmpegExecutable("ffmpeg")) {
            cachedFfmpegPath = "ffmpeg";
            ffmpegAvailable = true;
            return cachedFfmpegPath;
        }

        // 3. Dynamic search in WinGet Packages directory
        String userHome = System.getProperty("user.home", "");
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null || localAppData.trim().isEmpty()) {
            localAppData = userHome + File.separator + "AppData" + File.separator + "Local";
        }

        File wingetDir = new File(localAppData, "Microsoft\\WinGet\\Packages");
        if (wingetDir.exists() && wingetDir.isDirectory()) {
            File discovered = findFfmpegRecursive(wingetDir, 4);
            if (discovered != null && checkFfmpegExecutable(discovered.getAbsolutePath())) {
                cachedFfmpegPath = discovered.getAbsolutePath();
                ffmpegAvailable = true;
                return cachedFfmpegPath;
            }
        }

        // 4. Known common candidate paths (Windows, Linux, macOS)
        String[] candidatePaths = {
            // WinGet links
            localAppData + "\\Microsoft\\WinGet\\Links\\ffmpeg.exe",
            // Program Files & direct drives
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
                    return cachedFfmpegPath;
                }
            }
        }

        ffmpegAvailable = false;
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
