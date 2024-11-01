package dataaccess;

import model.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;



public class SqlUserDAO extends SqlBase implements UserDAO {

    public SqlUserDAO() {
        String[] statements = {
                """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """

        };
        configureDatabase(statements);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE user";
        executeUpdate(statement);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("username can't be empty");
        }
        UserData singleResult = executeQuerySingle(
                "SELECT username, password, email FROM user WHERE username=?",
                rs -> {
                    try {
                        return readUser(rs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                username
        );
        return singleResult;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var user = rs.getString("username");  // Can use column names for readability
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(user, password, email);
    }

    @Override
    public int length() throws DataAccessException {
        int length = executeQuerySingle(
                "SELECT COUNT(*) FROM user",
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

