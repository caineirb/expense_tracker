package Model.Transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.Database.DatabaseConnector;
import Utils.ErrorMessages;

public class TransactionService {
    Connection conn = null;

    public TransactionService() {
        try {
            this.conn = DatabaseConnector.connect();
        } catch (SQLException se) {
            ErrorMessages.printError(getClass(), se, "SQL Exception: ");
            this.conn = null;
        }
    }

    public void createTransaction(String name, String description, String location) throws SQLException {
        createTransaction(name, description, location, null);
    }

    public void createTransaction(String name, String description, String location, String date) throws SQLException {
        this.checkConnection();

        String sqlString = (date == null)
                ? "INSERT INTO \"transactions\" (name, description, location) VALUE (?, ?, ?);"
                : "INSERT INTO \"transactions\" (name, description, location, date) VALUE (?, ?, ?, ?);";
        this.conn.setAutoCommit(false);
        try (PreparedStatement stmt = this.conn.prepareStatement(sqlString)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, location);
            if (date != null)
                stmt.setString(4, date);
            stmt.executeUpdate();
            this.conn.commit();
        } catch (SQLException se) {
            this.conn.rollback();
            throw new SQLException(se);
        }
    }

    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            this.checkConnection();

            String sqlQuery = """
                    SELECT name, description, location, date
                    FROM "transactions"
                    ORDER BY date;
                    """;

            PreparedStatement stmt = this.conn.prepareStatement(sqlQuery);
            ResultSet result = stmt.executeQuery();
            int count = 1;
            while (result.next()) {
                transactions.add(new Transaction(
                        count,
                        result.getString("name"),
                        result.getString("description"),
                        result.getString("date"),
                        result.getString("location")));
                count++;
            }

        } catch (SQLException se) {
            ErrorMessages.printError(getClass(), se, "SQL Exception: ");
        } catch (Exception e) {
            ErrorMessages.printError(getClass(), e);
        }
        return transactions;
    }

    private void checkConnection() throws SQLException {
        if (this.conn == null)
            throw new SQLException("No SQL Connection Available.");
    }
}
