package com.kasnet.util;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Server-Side Input Validation & Sanitization Utility
 *
 * Centralises all form-input validation logic for the Kasnet Online Internship Portal.
 * Every public method is stateless and thread-safe.
 *
 * Usage:
 *   if (!ValidationUtil.isValidEmail(email)) { ... }
 *   String safe = ValidationUtil.sanitize(userInput);
 */
public class ValidationUtil {

    // -------------------------------------------------------------------------
    // Pre-compiled regex patterns (thread-safe after static initialisation)
    // -------------------------------------------------------------------------

    /** RFC 5322-inspired email pattern — covers most real-world email formats. */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*"
        + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Indian mobile number: starts with 6, 7, 8, or 9 and is exactly 10 digits.
     * Optional leading +91 or 0 prefix is also accepted.
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "^(?:\\+91|0)?[6-9]\\d{9}$"
    );

    /** Exactly 4 consecutive digits, e.g. 2024. */
    private static final Pattern YEAR_PATTERN = Pattern.compile(
        "^\\d{4}$"
    );

    /**
     * Strong password:
     *   - At least 8 characters
     *   - At least one uppercase letter  [A-Z]
     *   - At least one digit             [0-9]
     *   - At least one special character [!@#$%^&*()_+\-=\[\]{}|;':",.<>/?`~\\]
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?`~\\\\]).{8,}$"
    );

    // Private constructor — utility class
    private ValidationUtil() {
        throw new UnsupportedOperationException("ValidationUtil is a utility class.");
    }

    // -------------------------------------------------------------------------
    // Validation methods
    // -------------------------------------------------------------------------

    /**
     * Validates an email address using a robust regex pattern.
     *
     * @param email the email string to validate
     * @return {@code true} if the email format is valid and non-null/non-empty
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a 10-digit mobile number.
     * Must be exactly 10 numeric digits.
     *
     * @param mobile the mobile number string to validate
     * @return {@code true} if exactly 10 digits
     */
    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }
        return mobile.trim().matches("^[0-9]{10}$");
    }

    /**
     * Validates that a date string is in valid yyyy-MM-dd format and not in the future.
     *
     * @param dateStr the date string to validate
     * @return {@code true} if valid date in the past
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date d = sdf.parse(dateStr.trim());
            java.util.Date now = new java.util.Date();
            java.util.Calendar minCal = java.util.Calendar.getInstance();
            minCal.set(1920, java.util.Calendar.JANUARY, 1);
            return d.before(now) && d.after(minCal.getTime());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks that a string is not null and not blank (whitespace-only strings
     * are considered empty).
     *
     * @param value the string to test
     * @return {@code true} if the value has at least one non-whitespace character
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates that the supplied string represents a plausible 4-digit academic year
     * (e.g. 2020, 2024, 2030).
     *
     * @param year the year string to validate
     * @return {@code true} if it is exactly 4 digits
     */
    public static boolean isValidYear(String year) {
        if (year == null || year.trim().isEmpty()) {
            return false;
        }
        if (!YEAR_PATTERN.matcher(year.trim()).matches()) {
            return false;
        }
        int y = Integer.parseInt(year.trim());
        // Sanity range: 1900 – 2100
        return y >= 1900 && y <= 2100;
    }

    /**
     * Validates that a percentage value is a number in the range [0.0, 100.0].
     *
     * @param pct the percentage string (e.g. "85", "92.5")
     * @return {@code true} if it parses to a double in [0, 100]
     */
    public static boolean isValidPercentage(String pct) {
        if (pct == null || pct.trim().isEmpty()) {
            return false;
        }
        try {
            double value = Double.parseDouble(pct.trim());
            return value >= 0.0 && value <= 100.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates password strength:
     * <ul>
     *   <li>Minimum 8 characters</li>
     *   <li>At least one uppercase letter</li>
     *   <li>At least one digit</li>
     *   <li>At least one special character</li>
     * </ul>
     *
     * @param pwd the plain-text password to validate
     * @return {@code true} if the password meets all strength requirements
     */
    public static boolean isValidPassword(String pwd) {
        if (pwd == null || pwd.length() < 8) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(pwd).matches();
    }

    /**
     * Sanitizes user input by:
     * <ol>
     *   <li>Trimming leading/trailing whitespace</li>
     *   <li>Escaping HTML entities to prevent XSS injection
     *       ({@code &}, {@code <}, {@code >}, {@code "}, {@code '})</li>
     * </ol>
     *
     * <p><strong>Note:</strong> This is a first-line defence. For rich-text content,
     * use a dedicated HTML sanitizer library such as OWASP Java HTML Sanitizer.</p>
     *
     * @param input the raw user-supplied string (may be null)
     * @return a sanitized string, or an empty string if input was null
     */
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim()
                    .replace("&",  "&amp;")   // must be first to avoid double-escaping
                    .replace("<",  "&lt;")
                    .replace(">",  "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'",  "&#x27;");
    }

    /**
     * Checks whether a file's extension is present in the given list of
     * allowed extensions (case-insensitive comparison).
     *
     * @param fileName          the original file name (e.g. "resume.pdf")
     * @param allowedExtensions an array of permitted extensions without dots
     *                          (e.g. {@code {"pdf", "doc", "docx"}})
     * @return {@code true} if the file's extension matches one of the allowed values
     */
    public static boolean isValidFileType(String fileName, String[] allowedExtensions) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return false;
        }
        String ext = FileUtil.getFileExtension(fileName).toLowerCase();
        if (ext.isEmpty()) {
            return false;
        }
        for (String allowed : allowedExtensions) {
            if (allowed != null && allowed.trim().equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
}
