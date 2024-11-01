package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
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
    public void clear() throws DataAccessException {
        sqlGameDAO.clear();
        assertEquals(0, sqlGameDAO.length());
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO game " +
                    "(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                ChessGame game1 = new ChessGame();
                var jsonGame = new Gson().toJson(game1);
                ps.setInt(1, 12345);
                ps.setString(2, "user");
                ps.setString(3, null);
                ps.setString(4, "game1");
                ps.setString(5, jsonGame);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        assertEquals(1, sqlGameDAO.length());
        sqlGameDAO.clear();
        assertEquals(0, sqlGameDAO.length());
    }

    @Test
    public void createGame() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        var jsonGame = new Gson().toJson(game1);
        GameData testGame = new GameData(12345, "user1", null, "game1", game1);
        sqlGameDAO.createGame(testGame);
        assertEquals(1, sqlGameDAO.length());
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals(12345, rs.getInt(1));
                        assertEquals("user1", rs.getString(2));
                        assertNull(rs.getString(3));
                        assertEquals("game1", rs.getString(4));
                        assertEquals(jsonGame, rs.getString(5));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void createGameNegative() throws DataAccessException {
        GameData testGame = new GameData(0, null, null, null, null);
        assertThrows(DataAccessException.class, () -> sqlGameDAO.createGame(testGame));
    }

    @Test
    public void deleteGamePositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        var jsonGame = new Gson().toJson(game1);
        String[][] params = {{"12345", "user", null, "game1", jsonGame}};
        try (var conn = DatabaseManager.getConnection()) {
            for (String[] item : params) {
                try (var ps = conn.prepareStatement("INSERT INTO game " +
                        "(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {

                    ps.setInt(1, Integer.parseInt(item[0]));
                    ps.setString(2, item[1]);
                    ps.setString(3, item[2]);
                    ps.setString(4, item[3]);
                    ps.setString(5, item[4]);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        assertEquals(1, sqlGameDAO.length());
        sqlGameDAO.removeGame(12345);
        assertEquals(0, sqlGameDAO.length());
    }

    @Test
    public void deleteGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> sqlGameDAO.removeGame(null));
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        // Test getting the game when the gameID isn't in the database
        GameData test1 = sqlGameDAO.getGame(12345);
        assertNull(test1);
        // Test getting the game when the gameID IS in the database
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO game " +
                    "(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                ChessGame game1 = new ChessGame();
                var jsonGame = new Gson().toJson(game1);
                ps.setInt(1, 12345);
                ps.setString(2, "user");
                ps.setString(3, null);
                ps.setString(4, "game1");
                ps.setString(5, jsonGame);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        GameData test2 = sqlGameDAO.getGame(12345);
        ChessGame newGame = new ChessGame();
        GameData expectedGame = new GameData(12345, "user", null, "game1", newGame);
        assertEquals(expectedGame, test2);
    }

    @Test
    public void getGameNegative() {
        assertThrows(DataAccessException.class, () -> sqlGameDAO.getGame(null));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        var jsonGame = new Gson().toJson(game1);
        String[][] params = {{"12345", "user", null, "game1", jsonGame}, {"56789", "user2", "user3", "game2", jsonGame}};
        try (var conn = DatabaseManager.getConnection()) {
            for (String[] item : params) {
                try (var ps = conn.prepareStatement("INSERT INTO game " +
                        "(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {

                    ps.setInt(1, Integer.parseInt(item[0]));
                    ps.setString(2, item[1]);
                    ps.setString(3, item[2]);
                    ps.setString(4, item[3]);
                    ps.setString(5, item[4]);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        ArrayList<GameData> expectedList = new ArrayList<>();
        GameData expected1 = new GameData(12345, "user", null, "game1", game1);
        GameData expected2 = new GameData(56789, "user2", "user3", "game2", game1);
        expectedList.add(expected1);
        expectedList.add(expected2);
        assertEquals(expectedList, sqlGameDAO.listGames());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        assertEquals(new ArrayList<GameData>(), sqlGameDAO.listGames());
    }


    @Test
    public void lengthPositive() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO game " +
                    "(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)")) {
                ChessGame game1 = new ChessGame();
                var jsonGame = new Gson().toJson(game1);
                ps.setInt(1, 12345);
                ps.setString(2, "user");
                ps.setString(3, null);
                ps.setString(4, "game1");
                ps.setString(5, jsonGame);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        assertEquals(1, sqlGameDAO.length());
    }
}
