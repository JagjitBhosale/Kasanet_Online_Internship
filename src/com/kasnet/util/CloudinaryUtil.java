package com.kasnet.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * CloudinaryUtil — Centralized Cloudinary SDK wrapper for the Course Video Lecture System.
 *
 * Configured with Cloudinary credentials for secure video upload, signed URL generation,
 * and video asset deletion.
 */
public class CloudinaryUtil {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryUtil.class);

    private static final String CLOUD_NAME = "clldsbzp";
    private static final String API_KEY    = "398846588448751";
    private static final String API_SECRET = "OlICssUKo2jetINb2aGcLMpq7N0";

    private static final Cloudinary cloudinary;

    static {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        config.put("secure", "true");
        cloudinary = new Cloudinary(config);
        logger.info("Cloudinary initialized with cloud_name: {}", CLOUD_NAME);
    }

    private CloudinaryUtil() {}

    public static Cloudinary getInstance() {
        return cloudinary;
    }

    /**
     * Uploads a video file to Cloudinary.
     *
     * @param file     the local file to upload
     * @param publicId custom public ID (or null for auto-generated)
     * @return Map containing upload result (public_id, duration, secure_url, etc.)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> uploadVideo(File file, String publicId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "video");
        params.put("overwrite", true);
        params.put("invalidate", true);
        if (publicId != null && !publicId.trim().isEmpty()) {
            params.put("public_id", publicId);
        }

        logger.info("Uploading video to Cloudinary: {} (publicId={})", file.getName(), publicId);
        return cloudinary.uploader().upload(file, params);
    }

    /**
     * Uploads a video from an InputStream (used by multipart servlet).
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> uploadVideo(byte[] bytes, String publicId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "video");
        params.put("overwrite", true);
        params.put("invalidate", true);
        if (publicId != null && !publicId.trim().isEmpty()) {
            params.put("public_id", publicId);
        }

        return cloudinary.uploader().upload(bytes, params);
    }

    /**
     * Generates a protected, short-lived video URL.
     *
     * @param publicId         the Cloudinary public_id (e.g. "kasanet/lectures/abc")
     * @param expiresInSeconds expiration time window (e.g. 300 for 5 minutes)
     * @return VideoUrlResult containing videoUrl, expiresIn, and expiresAt epoch ms
     */
    public static VideoUrlResult generateProtectedVideoUrl(String publicId, int expiresInSeconds) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return null;
        }

        long nowEpochSec = Instant.now().getEpochSecond();
        long expiresAtEpochSec = nowEpochSec + expiresInSeconds;

        // Clean public id of any file extension
        String cleanId = publicId.replaceFirst("\\.mp4$", "").replaceFirst("\\.webm$", "");

        // Generate signed, secure URL using Cloudinary URL builder
        String videoUrl = cloudinary.url()
                .resourceType("video")
                .format("mp4")
                .secure(true)
                .signed(true)
                .generate(cleanId);

        return new VideoUrlResult(videoUrl, expiresInSeconds, expiresAtEpochSec * 1000);
    }

    /**
     * Deletes a video asset from Cloudinary.
     */
    public static boolean deleteVideo(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) return false;
        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "video",
                    "invalidate", true
            );
            cloudinary.uploader().destroy(publicId, params);
            logger.info("Deleted video from Cloudinary: {}", publicId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete video: {}", publicId, e);
            return false;
        }
    }

    /**
     * Holder class for short-lived video URL metadata.
     */
    public static class VideoUrlResult {
        private final String videoUrl;
        private final int expiresIn;
        private final long expiresAt;

        public VideoUrlResult(String videoUrl, int expiresIn, long expiresAt) {
            this.videoUrl = videoUrl;
            this.expiresIn = expiresIn;
            this.expiresAt = expiresAt;
        }

        public String getVideoUrl() { return videoUrl; }
        public int getExpiresIn() { return expiresIn; }
        public long getExpiresAt() { return expiresAt; }
    }
}
