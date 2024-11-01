package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SqlAuthTest {
    private static SqlAuthDAO sqlAuthDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        sqlAuthDAO = new SqlAuthDAO();
    }

    @BeforeEach
    public void deleteTable() throws DataAccessException {
        dropAuthTable();
        sqlAuthDAO = new SqlAuthDAO();
    }

    private void dropAuthTable() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DROP TABLE auth")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete table");
        }
    }

    @Test
    public void clear() throws DataAccessException {
        sqlAuthDAO.clear();
        assertEquals(0, sqlAuthDAO.length());

        insertAuthData("12345", "user");
        assertEquals(1, sqlAuthDAO.length());

        sqlAuthDAO.clear();
        assertEquals(0, sqlAuthDAO.length());
    }

    private void insertAuthData(String authToken, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, authToken);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthData testAuth = new AuthData("12345", "user");
        sqlAuthDAO.createAuth(testAuth);
        assertEquals(1, sqlAuthDAO.length());
        validateAuthData("12345", "user");
    }

    private void validateAuthData(String expectedAuthToken, String expectedUsername) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth";
            try (var ps = conn.prepareStatement(statement);
                 var rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals(expectedAuthToken, rs.getString(1));
                    assertEquals(expectedUsername, rs.getString(2));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        AuthData testAuth = new AuthData(null, null);
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.createAuth(testAuth));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        assertNull(sqlAuthDAO.getAuth("12345"));

        insertAuthData("12345", "user");
        AuthData testAuth = new AuthData("12345", "user");
        assertEquals(testAuth, sqlAuthDAO.getAuth("12345"));
    }

    @Test
    public void getAuthNegative() {
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth(null));
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth(""));
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        insertAuthData("12345", "user");
        insertAuthData("56789", "user2");

        sqlAuthDAO.deleteAuth("12345");
        assertEquals(1, sqlAuthDAO.length());
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.deleteAuth(null));
    }

    @Test
    public void lengthPositive() throws DataAccessException {
        insertAuthData("12345", "user");
        assertEquals(1, sqlAuthDAO.length());
    }
}
