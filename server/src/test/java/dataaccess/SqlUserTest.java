package dataaccess;


import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SqlUserTest {
    private static SqlUserDAO sqlUserDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        sqlUserDAO = new SqlUserDAO();
    }
    @BeforeEach
    public void resetTable() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DROP TABLE user")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to reset table");
        }
        sqlUserDAO = new SqlUserDAO();
    }



    private void insertUser(String username, String password, String email) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert user");
        }
    }

    @Test
    public void clear() throws DataAccessException {
        insertUser("user", "pass", "mail.com");
        assertEquals(1, sqlUserDAO.length());
        sqlUserDAO.clear();
        assertEquals(0, sqlUserDAO.length());
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        UserData testUser = new UserData("user", "password", "email@mail.com");
        sqlUserDAO.createUser(testUser);
        assertEquals(1, sqlUserDAO.length());
        UserData retrievedUser = sqlUserDAO.getUser("user");
        assertEquals(testUser, retrievedUser);
    }

    @Test
    public void createUserNegative() {
        UserData invalidUser = new UserData(null, null, null);
        assertThrows(DataAccessException.class, () -> sqlUserDAO.createUser(invalidUser));
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        assertNull(sqlUserDAO.getUser("user"));
        insertUser("user", "pass", "mail.com");
        UserData testUser = new UserData("user", "pass", "mail.com");
        assertEquals(testUser, sqlUserDAO.getUser("user"));
    }

    @Test
    public void getUserNegative() {
        assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser(null));
        assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser(""));
    }

    @Test
    public void lengthPositive() throws DataAccessException {
        insertUser("user", "pass", "mail.com");
        assertEquals(1, sqlUserDAO.length());
    }

}
