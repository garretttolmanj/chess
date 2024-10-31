package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlGameDAO implements GameDAO{

    public SqlGameDAO() throws DataAccessException {
        configureDatabase();
    }


    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE game";
        executeUpdate(statement);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var id = rs.getInt(1);
                        var whiteUsername = rs.getString(2);
                        var blackUsername = rs.getString(3);
                        var gameName = rs.getString(4);
                        var jsonGame = rs.getString(5);
                        var game = new Gson().fromJson(jsonGame, ChessGame.class);
                        gameList.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameList;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var jsonGame = new Gson().toJson(game.game());
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), jsonGame);
    }

    @Override
    public void removeGame(Integer gameID) throws DataAccessException {
        if (gameID == null) {
            throw new DataAccessException("authToken can't be empty");
        }
        var statement = "DELETE FROM game WHERE gameID =?";
        executeUpdate(statement, gameID);
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        if (gameID == null) {
            throw new DataAccessException("gameID can't be empty");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var id = rs.getInt(1);
                        var whiteUsername = rs.getString(2);
                        var blackUsername = rs.getString(3);
                        var gameName = rs.getString(4);
                        var jsonGame = rs.getString(5);
                        var game = new Gson().fromJson(jsonGame, ChessGame.class);
                        return new GameData(id, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public int length() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM game";
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` INT NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """

    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
