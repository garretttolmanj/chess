package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.BadRequestException;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;

public class SqlUserTest {
    private static SqlUserDAO sqlUserDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        sqlUserDAO = new SqlUserDAO();
    }
    @BeforeEach
    public void deleteTable() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DROP TABLE user")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete table");
        }
        sqlUserDAO = new SqlUserDAO();
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        UserData testUser = new UserData("user", "password", "email@mail.com");
        sqlUserDAO.createUser(testUser);
        assertEquals(1, sqlUserDAO.length());
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals("user", rs.getString(1));
                        assertEquals("password", rs.getString(2));
                        assertEquals("email@mail.com", rs.getString(3));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        UserData testUser = new UserData(null, null, null);
        assertThrows(DataAccessException.class, () -> sqlUserDAO.createUser(testUser));
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        // Test getting the user when the username isn't in the database
        UserData test1 = sqlUserDAO.getUser("user");
        assertNull(test1);
        // Test getting the user when the username IS in the database
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, "user");
                ps.setString(2, "pass");
                ps.setString(3, "mail.com");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        UserData test2 = sqlUserDAO.getUser("user");
        UserData testUser = new UserData("user", "pass", "mail.com");
        assertEquals(testUser, test2);
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser(null));
        assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser(""));
    }

    @Test
    public void lengthPositive() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, "user");
                ps.setString(2, "pass");
                ps.setString(3, "mail.com");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        assertEquals(1, sqlUserDAO.length());
    }
}
