package dataaccess;

import model.AuthData;
import model.UserData;
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
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DROP TABLE auth")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete table");
        }
        sqlAuthDAO = new SqlAuthDAO();
    }

    @Test
    public void clear() throws DataAccessException {
        sqlAuthDAO.clear();
        assertEquals(0, sqlAuthDAO.length());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, "12345");
                ps.setString(2, "user");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        assertEquals(1, sqlAuthDAO.length());
        sqlAuthDAO.clear();
        assertEquals(0, sqlAuthDAO.length());
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthData testAuth = new AuthData("12345", "user");
        sqlAuthDAO.createAuth(testAuth);
        assertEquals(1, sqlAuthDAO.length());
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals("12345", rs.getString(1));
                        assertEquals("user", rs.getString(2));
                    }
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
        // Test getting the auth when the authToken isn't in the database
        AuthData test1 = sqlAuthDAO.getAuth("12345");
        assertNull(test1);
        // Test getting the auth when the authToken IS in the database
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, "12345");
                ps.setString(2, "user");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        AuthData test2 = sqlAuthDAO.getAuth("12345");
        AuthData testAuth = new AuthData("12345", "user");
        assertEquals(testAuth, test2);
    }

    @Test
    public void getAuthNegative() {
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth(null));
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth(""));
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        String[][] params = {{"12345", "user"}, {"56789", "user2"}};
        try (var conn = DatabaseManager.getConnection()) {
            for (String[] item : params) {
                try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                    ps.setString(1, item[0]);
                    ps.setString(2, item[1]);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        sqlAuthDAO.deleteAuth("12345");
        assertEquals(1, sqlAuthDAO.length());
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> sqlAuthDAO.deleteAuth(null));
    }

    @Test
    public void lengthPositive() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, "12345");
                ps.setString(2, "user");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        assertEquals(1, sqlAuthDAO.length());
    }
}
