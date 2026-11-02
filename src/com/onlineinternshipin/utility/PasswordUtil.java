package com.onlineinternshipin.utility;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * PasswordUtil - BCrypt Password Hashing Utility
 *
 * Provides secure password hashing and verification using the BCrypt algorithm.
 * Supports legacy formats (plain-text, MD5, SHA-1) for backward compatibility
 * with pre-existing database records.
 */
public class PasswordUtil {

    private static final int ROUNDS = 12;

    private PasswordUtil() {
        throw new UnsupportedOperationException("PasswordUtil is a utility class.");
    }

    /**
     * Hashes a plain-text password using BCrypt with work factor of 12.
     */
    public static String hashPassword(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt(ROUNDS));
    }

    /**
     * Verifies a plain-text password against a hash or legacy password string.
     */
    public static boolean checkPassword(String plainText, String hash) {
        if (plainText == null || plainText.isEmpty()) {
            return false;
        }
        if (hash == null || hash.isEmpty()) {
            return false;
        }

        // 1. Check if string is a valid BCrypt hash ($2a$, $2b$, $2y$)
        if (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plainText, hash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // 2. Direct comparison for plain-text passwords (exact or trimmed)
        if (plainText.equals(hash) || plainText.equals(hash.trim())) {
            return true;
        }

        // 3. Fallback for 32-char hex (MD5)
        if (hash.trim().length() == 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : digest) {
                    hexString.append(String.format("%02x", b));
                }
                if (hexString.toString().equalsIgnoreCase(hash.trim())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        // 4. Fallback for 40-char hex (SHA-1)
        if (hash.trim().length() == 40) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : digest) {
                    hexString.append(String.format("%02x", b));
                }
                if (hexString.toString().equalsIgnoreCase(hash.trim())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        return false;
    }
}
