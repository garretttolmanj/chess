package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SqlGameTest {
    private static SqlGameDAO sqlGameDAO;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        sqlGameDAO = new SqlGameDAO();
    }

    @BeforeEach
    public void resetTable() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DROP TABLE game")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete table");
        }
        sqlGameDAO = new SqlGameDAO();
    }

    @AfterAll
    public static void clearData() throws DataAccessException {
        sqlGameDAO.clear();
    }

    private void insertGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, gameID);
                ps.setString(2, whiteUsername);
                ps.setString(3, blackUsername);
                ps.setString(4, gameName);
                ps.setString(5, new Gson().toJson(game));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to insert game");
        }
    }

    @Test
    public void clear() throws DataAccessException {
        insertGame(12345, "user", null, "game1", new ChessGame());
        assertEquals(1, sqlGameDAO.length());
        sqlGameDAO.clear();
        assertEquals(0, sqlGameDAO.length());
    }

    @Test
    public void createGame() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        GameData testGame = new GameData(12345, "user1", null, "game1", game1);
        sqlGameDAO.createGame(testGame);
        assertEquals(1, sqlGameDAO.length());
        GameData retrievedGame = sqlGameDAO.getGame(12345);
        assertEquals(testGame, retrievedGame);
    }

    @Test
    public void createGameNegative() {
        GameData testGame = new GameData(0, null, null, null, null);
        assertThrows(DataAccessException.class, () -> sqlGameDAO.createGame(testGame));
    }

    @Test
    public void deleteGamePositive() throws DataAccessException {
        insertGame(12345, "user", null, "game1", new ChessGame());
        assertEquals(1, sqlGameDAO.length());
        sqlGameDAO.removeGame(12345);
        assertEquals(0, sqlGameDAO.length());
    }

    @Test
    public void deleteGameNegative() {
        assertThrows(DataAccessException.class, () -> sqlGameDAO.removeGame(null));
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        insertGame(12345, "user", null, "game1", game1);
        GameData expectedGame = new GameData(12345, "user", null, "game1", game1);
        GameData test2 = sqlGameDAO.getGame(12345);
        assertEquals(expectedGame, test2);
    }

    @Test
    public void getGameNegative() {
        assertThrows(DataAccessException.class, () -> sqlGameDAO.getGame(12345));
        assertThrows(DataAccessException.class, () -> sqlGameDAO.getGame(null));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        insertGame(12345, "user", null, "game1", game1);
        insertGame(56789, "user2", "user3", "game2", game1);

        ArrayList<GameData> expectedList = new ArrayList<>();
        expectedList.add(new GameData(12345, "user", null, "game1", game1));
        expectedList.add(new GameData(56789, "user2", "user3", "game2", game1));

        assertEquals(expectedList, sqlGameDAO.listGames());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        assertEquals(new ArrayList<GameData>(), sqlGameDAO.listGames());
    }

    @Test
    public void lengthPositive() throws DataAccessException {
        insertGame(12345, "user", null, "game1", new ChessGame());
        assertEquals(1, sqlGameDAO.length());
    }
}
