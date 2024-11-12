package dataaccess;

import model.*;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SqlAuthDAO extends SqlBase implements AuthDAO {

    public SqlAuthDAO()  {
        String[] statements = {
                """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """};
        configureDatabase(statements);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE auth";
        executeUpdate(statement);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("authToken can't be empty");
        }

        AuthData auth = executeQuerySingle(
                "SELECT authToken, username FROM auth WHERE authToken=?",
                rs -> {
                    try {
                        return readAuth(rs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                authToken
        );

        return auth;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var token = rs.getString(1);
        var username = rs.getString(2);
        return new AuthData(token, username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("authToken can't be empty");
        }
        var statement = "DELETE FROM auth WHERE authToken =?";
        executeUpdate(statement, authToken);
    }

    @Override
    public int length() throws DataAccessException {
        int length = executeQuerySingle(
                "SELECT COUNT(*) FROM auth",
                rs -> {
                    try {
                        return rs.getInt(1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        return length;
    }

}
