package com.onlineinternshipin.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Database Connection Utility
 *
 * Provides a centralized, thread-safe mechanism to obtain JDBC connections
 * to the MySQL database for the Kasnet Online Internship Portal.
 * Supports local development (.env, properties) and CloudJiffy production (Environment Variables).
 *
 * Usage (try-with-resources recommended):
 *   try (Connection conn = DBConnection.getConnection()) {
 *       // use connection
 *   }
 */
public class DBConnection {

    // -------------------------------------------------------------------------
    // DATABASE CONFIGURATION — Dynamically loaded with production env var priority
    // -------------------------------------------------------------------------

    private static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/onlineinternship"
            + "?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "";

    /** Fully-qualified MySQL JDBC driver class name. */
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private static final java.util.Properties DB_PROPS = new java.util.Properties();
    static {
        try (java.io.InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (in != null) {
                DB_PROPS.load(in);
            }
        } catch (Exception ignored) {}
        if (DB_PROPS.isEmpty()) {
            try (java.io.InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("database1.properties")) {
                if (in != null) {
                    DB_PROPS.load(in);
                }
            } catch (Exception ignored) {}
        }
    }

    public static String getDbUrl() {
        // 1. Production environment variables & .env take highest priority
        String envUrl = EnvUtil.get("DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim();
        }
        // Support CloudJiffy / Containerized host, port, database env vars
        String host = EnvUtil.get("DB_HOST", EnvUtil.get("MYSQL_HOST"));
        if (host != null && !host.trim().isEmpty()) {
            String port = EnvUtil.get("DB_PORT", EnvUtil.get("MYSQL_PORT", "3306"));
            String dbName = EnvUtil.get("DB_NAME", EnvUtil.get("MYSQL_DATABASE", "onlineinternship"));
            return "jdbc:mysql://" + host.trim() + ":" + port.trim() + "/" + dbName.trim()
                    + "?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
        }
        // 2. Local properties file fallback
        String propUrl = DB_PROPS.getProperty("db.url", DB_PROPS.getProperty("url"));
        if (propUrl != null && !propUrl.trim().isEmpty()) return propUrl.trim();
        return DEFAULT_DB_URL;
    }

    public static String getDbUser() {
        // 1. Production environment variables & .env take highest priority
        String envUser = EnvUtil.get("DB_USER", EnvUtil.get("MYSQL_USER"));
        if (envUser != null && !envUser.trim().isEmpty()) {
            return envUser.trim();
        }
        // 2. Local properties file fallback
        String propUser = DB_PROPS.getProperty("db.user", DB_PROPS.getProperty("user", DB_PROPS.getProperty("username")));
        if (propUser != null && !propUser.trim().isEmpty()) return propUser.trim();
        return DEFAULT_DB_USER;
    }

    public static String getDbPassword() {
        // 1. Production environment variables & .env take highest priority
        String envPass = EnvUtil.get("DB_PASSWORD", EnvUtil.get("MYSQL_PASSWORD"));
        if (envPass != null) {
            return envPass.trim();
        }
        // 2. Local properties file fallback
        String propPass = DB_PROPS.getProperty("db.password", DB_PROPS.getProperty("password"));
        if (propPass != null) return propPass.trim();
        return DEFAULT_DB_PASSWORD;
    }

    public static String getMaskedConnectionInfo() {
        return "URL=" + getDbUrl() + ", User=" + getDbUser() + ", EnvSource=" + EnvUtil.getSource();
    }

    // -------------------------------------------------------------------------
    // Static initialiser — loads the JDBC driver once when the class is loaded
    // -------------------------------------------------------------------------
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                "MySQL JDBC Driver not found. "
                + "Ensure mysql-connector-java is on the classpath. "
                + "Cause: " + e.getMessage()
            );
        }
    }

    // Private constructor — utility class, not meant to be instantiated
    private DBConnection() {
        throw new UnsupportedOperationException("DBConnection is a utility class.");
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns a new {@link Connection} to the configured MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
    }

    /**
     * Convenience method: quietly closes a {@link Connection} if it is not null
     * and not already closed.
     */
    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("[DBConnection] Warning: failed to close connection — " + e.getMessage());
            }
        }
    }
}
