package com.kasnet.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil - BCrypt Password Hashing Utility
 *
 * Provides secure password hashing and verification using the BCrypt algorithm.
 * BCrypt is intentionally slow, making brute-force attacks computationally expensive.
 *
 * Maven dependency required in pom.xml:
 *   <dependency>
 *       <groupId>org.mindrot</groupId>
 *       <artifactId>jbcrypt</artifactId>
 *       <version>0.4</version>
 *   </dependency>
 *
 * Usage:
 *   String hash = PasswordUtil.hashPassword("mySecret123!");
 *   boolean ok   = PasswordUtil.checkPassword("mySecret123!", hash);
 */
public class PasswordUtil {

    /**
     * BCrypt work factor (cost parameter).
     * 2^ROUNDS iterations are performed during hashing.
     * 12 is the recommended minimum for production (≈ 250–300 ms on modern hardware).
     * Increase to 13 or 14 if server capacity allows.
     */
    private static final int ROUNDS = 12;

    // Private constructor — utility class, not meant to be instantiated
    private PasswordUtil() {
        throw new UnsupportedOperationException("PasswordUtil is a utility class.");
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Hashes a plain-text password using BCrypt with a work factor of {@value #ROUNDS}.
     *
     * <p>A cryptographically random salt is generated automatically by BCrypt
     * and embedded in the returned hash string — you do <em>not</em> need to
     * store the salt separately.</p>
     *
     * @param plainText the raw password entered by the user (must not be null or empty)
     * @return a BCrypt hash string (60 characters, e.g. {@code $2a$12$...})
     * @throws IllegalArgumentException if {@code plainText} is null or empty
     */
    public static String hashPassword(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty.");
        }
        // BCrypt.gensalt(ROUNDS) generates a random salt encoded with the work factor
        return BCrypt.hashpw(plainText, BCrypt.gensalt(ROUNDS));
    }

    /**
     * Verifies a plain-text password against a previously computed BCrypt hash.
     *
     * <p>This method is safe against timing attacks because BCrypt's comparison
     * always takes roughly the same amount of time regardless of how early the
     * strings differ.</p>
     *
     * @param plainText the raw password entered by the user
     * @param hash      the BCrypt hash stored in the database
     * @return {@code true} if the password matches the hash; {@code false} otherwise
     */
    public static boolean checkPassword(String plainText, String hash) {
        if (plainText == null || plainText.isEmpty()) {
            return false;
        }
        if (hash == null || hash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainText, hash);
        } catch (IllegalArgumentException e) {
            // Thrown if the hash string is malformed (not a valid BCrypt hash)
            System.err.println("[PasswordUtil] Invalid hash format: " + e.getMessage());
            return false;
        }
    }
}
