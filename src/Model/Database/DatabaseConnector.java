package Model.Database;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import Controller.Auth.AuthFailedPopup;
import Utils.ErrorMessages;
import javafx.stage.Stage;

public class DatabaseConnector {
    private static final String DB_URL = "jdbc:sqlite:src/Database/init.db";
    private static final String DB_INIT = "src/Database/init.sql";
    private static final Logger logger = Logger.getLogger(DatabaseConnector.class.getName());
    private static Connection connection = null;

    private static void createConnection(String password) throws SQLException, SQLTimeoutException {
        if (DatabaseConnector.connection == null) {
            Properties conn_props = new Properties();
            conn_props.setProperty("password", password);
            connection = DriverManager.getConnection(DB_URL, conn_props);
        }
    }

    public static Connection connect() throws SQLException {
        if (DatabaseConnector.connection != null)
            return DatabaseConnector.connection;
        else
            throw new SQLException("Connection not created. Call `DatabaseConnector.createConnection` first.");
    }

    public static void initializeDB(String password, Stage stage) {
        try {
            DatabaseConnector.createConnection(password);
            Connection connection = DatabaseConnector.connect();

            connection.setAutoCommit(false);

            Statement statement = DatabaseConnector.buildDatabase();
            statement.executeBatch();
            connection.commit();
            logger.info(DB_INIT + " execution done successfully.");
        } catch (SQLException se) {
            if (se.getErrorCode() != 26) {
                try {
                    if (connection != null && !connection.getAutoCommit()) {
                        connection.rollback();
                        ErrorMessages.printError(DatabaseConnector.class.getClass(), se, "SQL Exception: ");
                        logger.info("Transaction rolled back.");
                    }
                } catch (SQLException rollbackEx) {
                    ErrorMessages.printError(DatabaseConnector.class.getClass(), rollbackEx, "Rollback failed: ");
                }
            } else {
                AuthFailedPopup.showPopup(stage);
            }
        } catch (InvalidPathException ie) {
            ErrorMessages.printError(DatabaseConnector.class.getClass(), ie, "Invalid Path Exception: ");
        } catch (OutOfMemoryError oe) {
            ErrorMessages.printError(DatabaseConnector.class.getClass(), oe, "Out of Memory Exception: ");
        } catch (Exception e) {
            ErrorMessages.printError(DatabaseConnector.class.getClass(), e);
        }
    }

    private static Statement buildDatabase() throws InvalidPathException, IOException, SQLException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(DB_INIT));
        Statement statement = connection.createStatement();

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
                    logger.info("Statement added to batch: " + sqlTask.substring(0, Math.min(sqlTask.length(), 40))
                            + "...");
                    statement.addBatch(sqlTask);
                }
                currentStatement.setLength(0); // Reset buffer for next statement
            }
        }
        reader.close();
        return statement;
    }
}
