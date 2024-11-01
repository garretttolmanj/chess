package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Function;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlBase {

    public void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                setParameters(ps, params);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public <R> ArrayList<R> executeQueryList(String statement, Function<ResultSet, R> function, Object... params) throws DataAccessException {
        ArrayList<R> results = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            setParameters(ps, params); // Set parameters safely

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(function.apply(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error executing query: " + e.getMessage());
        }
        return results;
    }

    // Method to retrieve a single result with parameter sanitization
    public <R> R executeQuerySingle(String statement, Function<ResultSet, R> function, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            setParameters(ps, params); // Set parameters safely

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return function.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error executing query: " + e.getMessage());
        }
        return null;
    }

    // Helper method to set parameters in the prepared statement
    private void setParameters(java.sql.PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case String s -> ps.setString(i + 1, s);
                case Integer integer -> ps.setInt(i + 1, integer);
                case Double v -> ps.setDouble(i + 1, v);
                case Boolean b -> ps.setBoolean(i + 1, b);
                case null -> ps.setNull(i + 1, java.sql.Types.NULL);
                default -> throw new SQLException("Unsupported parameter type: " + param.getClass().getName());
            }
        }
    }


    public void configureDatabase(String[] statements) {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
