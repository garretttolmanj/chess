package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clear() throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    void removeGame(Integer gameID) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    int length() throws DataAccessException;
//    createGame: Create a new game.
//    getGame: Retrieve a specified game with the given game ID.
//    updateGame: Updates a chess game.
//    It should replace the chess game string corresponding to a given gameID.
//    This is used when players join a game or when a move is made.
}
