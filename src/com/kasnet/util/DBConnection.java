package com.kasnet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Database Connection Utility
 *
 * Provides a centralized, thread-safe mechanism to obtain JDBC connections
 * to the MySQL database for the Kasnet Online Internship Portal.
 *
 * Usage (try-with-resources recommended):
 *   try (Connection conn = DBConnection.getConnection()) {
 *       // use connection
 *   }
 */
public class DBConnection {

    // -------------------------------------------------------------------------
    // DATABASE CONFIGURATION — Dynamically loaded from .env and environment
    // -------------------------------------------------------------------------

    private static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/online_internship"
            + "?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "";

    /** Fully-qualified MySQL JDBC driver class name. */
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    public static String getDbUrl() {
        return EnvUtil.get("DB_URL", DEFAULT_DB_URL);
    }

    public static String getDbUser() {
        return EnvUtil.get("DB_USER", DEFAULT_DB_USER);
    }

    public static String getDbPassword() {
        return EnvUtil.get("DB_PASSWORD", DEFAULT_DB_PASSWORD);
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
            /*
             * If this exception is thrown, the MySQL Connector/J JAR is missing
             * from the classpath / Maven dependencies.
             * Add to pom.xml:
             *   <dependency>
             *       <groupId>mysql</groupId>
             *       <artifactId>mysql-connector-java</artifactId>
             *       <version>8.0.33</version>
             *   </dependency>
             */
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
     *
     * <p>This method is thread-safe because {@link DriverManager#getConnection}
     * is synchronised internally and each call returns a fresh connection.
     * For high-traffic production use, replace with a connection pool such as
     * HikariCP or Apache DBCP.</p>
     *
     * <p><strong>Always</strong> use this inside a try-with-resources block so
     * the connection is closed automatically:</p>
     * <pre>{@code
     *   try (Connection conn = DBConnection.getConnection()) {
     *       // work with conn
     *   } catch (SQLException e) {
     *       // handle error
     *   }
     * }</pre>
     *
     * @return a live {@link Connection} object
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
    }

    /**
     * Convenience method: quietly closes a {@link Connection} if it is not null
     * and not already closed.  Useful in finally-blocks when try-with-resources
     * cannot be used.
     *
     * @param connection the connection to close (may be {@code null})
     */
    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Log and swallow — nothing useful can be done at this point
                System.err.println("[DBConnection] Warning: failed to close connection — " + e.getMessage());
            }
        }
    }
}
