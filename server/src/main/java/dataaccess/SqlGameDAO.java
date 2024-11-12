package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SqlGameDAO extends SqlBase implements GameDAO{

    public SqlGameDAO()  {
        String[] statements = {
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
        configureDatabase(statements);
    }


    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE game";
        executeUpdate(statement);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> gameList = executeQueryList(
                "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game",
                rs -> {
                    try {
                        return readGame(rs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

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
        GameData singleGame = executeQuerySingle(
                "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?",
                rs -> {
                    try {
                        return readGame(rs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                gameID
        );

        return singleGame;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt(1);
        var whiteUsername = rs.getString(2);
        var blackUsername = rs.getString(3);
        var gameName = rs.getString(4);
        var jsonGame = rs.getString(5);
        var game = new Gson().fromJson(jsonGame, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public int length() throws DataAccessException {
        int length = executeQuerySingle(
                "SELECT COUNT(*) FROM game",
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
