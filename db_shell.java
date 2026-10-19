import java.sql.*;
import java.util.*;

/**
 * Minimal interactive SQLite shell using the project's JDBC driver.
 *
 * Usage (from project root):
 * java --source 11 -cp "lib/sqlite-jdbc-3.53.4.0.jar" db_shell.java [db-file]
 * [password]
 *
 * Examples:
 * java --source 11 -cp "lib/sqlite-jdbc-3.53.4.0.jar" db_shell.java
 * src/Database/init.db 1
 */
public class db_shell {
    public static void main(String[] args) throws Exception {
        String dbFile = args.length > 0 ? args[0] : "src/Database/init.db";
        String password = args.length > 1 ? args[1] : "";
        boolean diagMode = args.length > 2 && args[2].equals("--diag");

        // Must match DatabaseConnector.java's DB_URL cipher parameters exactly
        String url = "jdbc:sqlite:" + dbFile + "?cipher=sqlcipher&legacy=4&hmac_algorithm=2&kdf_algorithm=2";
        Properties props = new Properties();
        if (!password.isEmpty()) {
            props.setProperty("password", password);
        }

        System.out.println("Connecting to: " + dbFile + " (cipher=sqlcipher)");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            System.err.println("Failed with cipher=sqlcipher: " + e.getMessage());
            System.out.println("Retrying without cipher param...");
            url = "jdbc:sqlite:" + dbFile;
            try {
                conn = DriverManager.getConnection(url, props);
                System.out.println("Connected (default cipher).");
            } catch (SQLException e2) {
                System.err.println("Connection failed: " + e2.getMessage());
                System.exit(1);
            }
        }

        if (diagMode) {
            // Print all cipher-related PRAGMAs for diagnostics
            System.out.println("\n=== Cipher Diagnostics ===");
            String[] pragmas = {
                    "PRAGMA cipher_version",
                    "PRAGMA cipher_settings",
                    "PRAGMA kdf_iter",
                    "PRAGMA cipher_page_size",
                    "PRAGMA cipher_use_hmac",
                    "PRAGMA cipher_hmac_algorithm",
                    "PRAGMA cipher_kdf_algorithm",
                    "PRAGMA cipher_legacy",
                    "PRAGMA page_size"
            };
            for (String pragma : pragmas) {
                try {
                    runQuerySimple(conn, pragma);
                } catch (Exception e) {
                    System.out.println(pragma + " -> (not supported)");
                }
            }
            System.out.println("==========================\n");
        }

        System.out.println("Connected. Type SQL statements ending with ';', or:");
        System.out.println("  .schema    - show all tables");
        System.out.println("  .tables    - list table names");
        System.out.println("  .diag      - show cipher diagnostics");
        System.out.println("  .quit      - exit");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        StringBuilder buffer = new StringBuilder();

        while (true) {
            System.out.print(buffer.length() == 0 ? "sqlite> " : "   ...> ");
            if (!scanner.hasNextLine())
                break;
            String line = scanner.nextLine().trim();

            if (buffer.length() == 0) {
                if (line.equals(".quit") || line.equals(".exit"))
                    break;

                if (line.equals(".tables")) {
                    runQuery(conn, "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;");
                    continue;
                }
                if (line.equals(".schema")) {
                    runQuery(conn, "SELECT sql FROM sqlite_master WHERE sql IS NOT NULL ORDER BY type, name;");
                    continue;
                }
                if (line.equals(".diag")) {
                    String[] pragmas = {
                            "PRAGMA cipher_version",
                            "PRAGMA kdf_iter",
                            "PRAGMA cipher_page_size",
                            "PRAGMA cipher_hmac_algorithm",
                            "PRAGMA cipher_kdf_algorithm",
                            "PRAGMA cipher_legacy",
                            "PRAGMA page_size"
                    };
                    System.out.println("\n=== Cipher Diagnostics ===");
                    for (String pragma : pragmas) {
                        try {
                            runQuerySimple(conn, pragma);
                        } catch (Exception e) {
                            System.out.println(pragma + " -> (not supported)");
                        }
                    }
                    System.out.println("==========================\n");
                    continue;
                }
            }

            buffer.append(line).append(" ");

            if (line.endsWith(";")) {
                String sql = buffer.toString().trim();
                buffer.setLength(0);
                try {
                    if (sql.toUpperCase().startsWith("SELECT") || sql.toUpperCase().startsWith("PRAGMA")) {
                        runQuery(conn, sql);
                    } else {
                        try (Statement stmt = conn.createStatement()) {
                            int affected = stmt.executeUpdate(sql);
                            System.out.println("OK (" + affected + " row(s) affected)");
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        conn.close();
    }

    private static void runQuerySimple(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            StringBuilder sb = new StringBuilder(sql + " -> ");
            while (rs.next()) {
                for (int i = 1; i <= meta.getColumnCount(); i++)
                    sb.append(rs.getString(i)).append(" ");
            }
            System.out.println(sb.toString().trim());
        }
    }

    private static void runQuery(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++)
                System.out.printf("%-30s", meta.getColumnName(i));
            System.out.println();
            System.out.println("-".repeat(30 * cols));
            int rows = 0;
            while (rs.next()) {
                for (int i = 1; i <= cols; i++)
                    System.out.printf("%-30s", rs.getString(i));
                System.out.println();
                rows++;
            }
            System.out.println("(" + rows + " row(s))");
        }
    }
}
