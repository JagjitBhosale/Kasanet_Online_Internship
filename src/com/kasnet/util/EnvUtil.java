package com.kasnet.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EnvUtil - Centralized Environment and Configuration Loader
 *
 * Automatically loads configuration from:
 * 1. Operating System Environment Variables (System.getenv) — highest priority (e.g. Docker, Heroku, AWS, Render)
 * 2. JVM System Properties (-Dkey=value)
 * 3. Local/Classpath .env file (Development, Embedded Tomcat, and Production WAR deployments)
 *
 * Thread-safe and zero-dependency.
 */
public final class EnvUtil {

    private static final Logger LOGGER = Logger.getLogger(EnvUtil.class.getName());

    private static final Map<String, String> ENV_VARS = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;
    private static String resolvedSource = "None";

    static {
        init();
    }

    private EnvUtil() {
        // Utility class
    }

    /**
     * Initializes and loads the .env configuration.
     * Can be safely called multiple times (subsequent calls will reload).
     */
    public static synchronized void init() {
        ENV_VARS.clear();
        resolvedSource = "None";

        // Try candidate locations in order of preference
        List<File> fileCandidates = new ArrayList<>();

        // 1. Explicitly configured path via JVM property or env var
        String explicitPath = System.getProperty("env.file");
        if (explicitPath == null || explicitPath.trim().isEmpty()) {
            explicitPath = System.getenv("ENV_FILE");
        }
        if (explicitPath != null && !explicitPath.trim().isEmpty()) {
            fileCandidates.add(new File(explicitPath.trim()));
        }

        // 2. Working directory (.env)
        fileCandidates.add(new File(".env"));
        fileCandidates.add(new File(System.getProperty("user.dir", "."), ".env"));

        // 3. Parent directory (for submodules/subdirectories)
        fileCandidates.add(new File("..", ".env"));

        // 4. Source & WebContent directories (Eclipse / IDE / Maven layouts)
        fileCandidates.add(new File("src", ".env"));
        fileCandidates.add(new File("WebContent", ".env"));
        fileCandidates.add(new File("WebContent/WEB-INF", ".env"));

        // 5. Tomcat server directories
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            fileCandidates.add(new File(catalinaBase, ".env"));
            fileCandidates.add(new File(catalinaBase, "conf/.env"));
        }

        boolean loadedFromFile = false;
        for (File candidate : fileCandidates) {
            if (candidate != null && candidate.isFile() && candidate.canRead()) {
                try (InputStream in = new FileInputStream(candidate)) {
                    loadFromInputStream(in);
                    resolvedSource = "File: " + candidate.getAbsolutePath();
                    LOGGER.info("[EnvUtil] Successfully loaded configuration from " + resolvedSource);
                    loadedFromFile = true;
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "[EnvUtil] Could not read file " + candidate.getAbsolutePath(), e);
                }
            }
        }

        // 6. If not loaded from filesystem, attempt Classpath (.env packaged in WAR / classes)
        if (!loadedFromFile) {
            String[] classpathResources = new String[] {
                ".env",
                "/.env",
                "env",
                "/env",
                "config.env",
                "/config.env"
            };

            for (String cpRes : classpathResources) {
                InputStream in = null;
                try {
                    // Try context class loader
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) {
                        in = cl.getResourceAsStream(cpRes.startsWith("/") ? cpRes.substring(1) : cpRes);
                    }
                    if (in == null) {
                        in = EnvUtil.class.getClassLoader().getResourceAsStream(cpRes.startsWith("/") ? cpRes.substring(1) : cpRes);
                    }
                    if (in == null) {
                        in = EnvUtil.class.getResourceAsStream(cpRes);
                    }

                    if (in != null) {
                        try {
                            loadFromInputStream(in);
                            resolvedSource = "Classpath: " + cpRes;
                            LOGGER.info("[EnvUtil] Successfully loaded configuration from " + resolvedSource);
                            loadedFromFile = true;
                            break;
                        } finally {
                            try { in.close(); } catch (Exception ignored) {}
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "[EnvUtil] Classpath lookup failed for " + cpRes, e);
                }
            }
        }

        if (!loadedFromFile) {
            LOGGER.info("[EnvUtil] No .env file found; operating strictly using OS environment variables and JVM system properties.");
        }

        initialized = true;
    }

    /**
     * Parses a .env format stream into the internal map.
     */
    private static void loadFromInputStream(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip empty lines and comment lines (# or //)
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                // Strip leading 'export ' if present
                if (line.startsWith("export ") && line.length() > 7) {
                    line = line.substring(7).trim();
                }

                int eqIdx = line.indexOf('=');
                if (eqIdx <= 0) {
                    continue;
                }

                String key = line.substring(0, eqIdx).trim();
                String value = line.substring(eqIdx + 1).trim();

                // Strip enclosing single or double quotes
                if ((value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) ||
                    (value.startsWith("'") && value.endsWith("'") && value.length() >= 2)) {
                    value = value.substring(1, value.length() - 1);
                }

                // Unescape common characters
                value = unescape(value);

                if (!key.isEmpty()) {
                    ENV_VARS.put(key, value);
                }
            }
        }
    }

    private static String unescape(String s) {
        if (s == null || s.indexOf('\\') < 0) return s;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case '"': sb.append('"'); i++; break;
                    case '\'': sb.append('\''); i++; break;
                    default: sb.append(c); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Force reloads the environment configuration.
     */
    public static synchronized void reload() {
        init();
    }

    /**
     * Returns the source from which .env was loaded (for diagnostics).
     */
    public static String getSource() {
        return resolvedSource;
    }

    /**
     * Retrieves the string configuration value for the given key.
     *
     * Resolution order:
     * 1. OS Environment Variable: System.getenv(key)
     * 2. JVM System Property: System.getProperty(key)
     * 3. Loaded .env property
     *
     * Also checks normalized uppercase (e.g. "db.user" -> "DB_USER")
     * and lowercase variants automatically.
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * Retrieves the configuration value for the given key, falling back
     * to defaultValue if not found or empty.
     */
    public static String get(String key, String defaultValue) {
        if (key == null || key.trim().isEmpty()) {
            return defaultValue;
        }
        if (!initialized) {
            init();
        }

        // Normalize variations: e.g. "db.user" <-> "DB_USER"
        String rawKey = key.trim();
        String upperSnake = rawKey.toUpperCase().replace('.', '_').replace('-', '_');
        String dotLower = rawKey.toLowerCase().replace('_', '.').replace('-', '.');

        // 1. OS Environment Variables
        String val = System.getenv(rawKey);
        if (isValid(val)) return val.trim();

        if (!rawKey.equals(upperSnake)) {
            val = System.getenv(upperSnake);
            if (isValid(val)) return val.trim();
        }

        // 2. JVM System Properties
        val = System.getProperty(rawKey);
        if (isValid(val)) return val.trim();

        val = System.getProperty(dotLower);
        if (isValid(val)) return val.trim();

        if (!rawKey.equals(upperSnake)) {
            val = System.getProperty(upperSnake);
            if (isValid(val)) return val.trim();
        }

        // 3. Loaded .env Variables
        val = ENV_VARS.get(rawKey);
        if (isValid(val)) return val.trim();

        val = ENV_VARS.get(upperSnake);
        if (isValid(val)) return val.trim();

        val = ENV_VARS.get(dotLower);
        if (isValid(val)) return val.trim();

        // Check case-insensitively in .env as last attempt
        for (Map.Entry<String, String> entry : ENV_VARS.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(rawKey) ||
                entry.getKey().equalsIgnoreCase(upperSnake)) {
                if (isValid(entry.getValue())) {
                    return entry.getValue().trim();
                }
            }
        }

        return defaultValue;
    }

    /**
     * Retrieves an integer value from configuration, or default if missing or unparseable.
     */
    public static int getInt(String key, int defaultValue) {
        String val = get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a boolean value from configuration.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val == null) return defaultValue;
        val = val.trim().toLowerCase();
        return "true".equals(val) || "1".equals(val) || "yes".equals(val) || "on".equals(val);
    }

    /**
     * Returns true if the key is present and non-empty in environment or .env.
     */
    public static boolean has(String key) {
        String val = get(key);
        return isValid(val);
    }

    private static boolean isValid(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
