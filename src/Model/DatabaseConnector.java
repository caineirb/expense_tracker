package Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;

public class DatabaseConnector {
    private static final String DB_URL = "jdbc:sqlite:src/Database/init.db";
    private static final String DB_INIT = "src/Database/init.sql";

    public DatabaseConnector() {
        Connection connection = DatabaseConnector.connect();
        DatabaseConnector.initializeDB(connection);
    }

    public static Connection connect() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLTimeoutException t) {
            System.out.println("Connection Timeout: " + t.getMessage());
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }

        return conn;
    }

    private static void initializeDB(Connection connection) {
        // Wrap in a try-with-resources to read line-by-line efficiently
        try (java.io.BufferedReader reader = Files.newBufferedReader(Paths.get(DB_INIT));
                Statement statement = connection.createStatement()) {

            connection.setAutoCommit(false);

            StringBuilder currentStatement = new StringBuilder();
            String line;
            boolean inMultiLineComment = false;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();

                // 1. Skip empty lines
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                // 2. Handle Multi-line comment block boundaries
                if (inMultiLineComment) {
                    if (trimmedLine.contains("*/")) {
                        inMultiLineComment = false;
                        // Keep text after the closing comment if any exists
                        trimmedLine = trimmedLine.substring(trimmedLine.indexOf("*/") + 2).trim();
                    } else {
                        continue; // Skip lines inside the comment block
                    }
                }
                if (trimmedLine.startsWith("/*")) {
                    if (!trimmedLine.contains("*/")) {
                        inMultiLineComment = true;
                        continue;
                    }
                    // If it starts and ends on the same line, strip it
                    trimmedLine = trimmedLine.replaceAll("/\\*.*?\\*/", "").trim();
                }

                // 3. Skip single-line comments
                if (trimmedLine.startsWith("--")) {
                    continue;
                }

                // Append current line to the statement builder
                currentStatement.append(line).append("\n");

                // 4. Batch check: Execute if the statement ends with a semicolon
                if (trimmedLine.endsWith(";")) {
                    String sqlTask = currentStatement.toString().trim();
                    if (!sqlTask.isEmpty()) {
                        System.out.println("Statement added to batch: "
                                + sqlTask.substring(0, Math.min(sqlTask.length(), 40)) + "...");
                        statement.addBatch(sqlTask);
                    }
                    currentStatement.setLength(0); // Reset buffer for next statement
                }
            }

            // Execute batch and commit transaction
            statement.executeBatch();
            connection.commit();
            System.out.println(DB_INIT + " execution done successfully.");

        } catch (SQLException se) {
            System.out.println("SQL Exception: " + se.getMessage());
            try {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                    System.out.println("Transaction rolled back.");
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } catch (InvalidPathException ie) {
            System.out.println("Invalid Path Exception: " + ie.getMessage());
        } catch (IOException io) {
            System.out.println("IO Exception: " + io.getMessage());
        } catch (OutOfMemoryError oe) {
            System.out.println("Out of Memory Exception: " + oe.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
