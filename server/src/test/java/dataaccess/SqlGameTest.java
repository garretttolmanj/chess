package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SqlGameTest {
    private static SqlGameDAO sqlGameDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        sqlGameDAO = new SqlGameDAO();
    }
    @BeforeEach
    public void deleteTable() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DROP TABLE game")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete table");
        }
        sqlGameDAO = new SqlGameDAO();
    }

    @Test
    public void createGame() throws DataAccessException {
        System.out.println("create Game Test");
    }
}
