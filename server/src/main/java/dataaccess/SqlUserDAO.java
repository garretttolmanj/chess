package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


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
    public void removeUser(String username) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("username can't be empty");
        }
        var statement = "DELETE FROM user WHERE username =?";
        executeUpdate(statement, username);
    }

    @Override
    public int length() throws DataAccessException {
//        int singleResult = executeQuerySingle(
//                "SELECT username, password, email FROM user WHERE username=?",
//                rs -> {
//                    try {
//                        return readUser(rs);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                username
//        );
//        return singleResult;

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM user";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) { // Move to the first row of the result
                        return rs.getInt(1); // Retrieve the count by index
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return 0; // Return 0 if no rows were found (unlikely with COUNT(*))
    }


//    private int executeQuery(String statement, Object... params) {
//        var result = new ArrayList<>();
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet";
//            try (var ps = conn.prepareStatement(statement)) {
//                try (var rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        result.add(readPet(rs));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return result;
//    }


}

