package dataaccess;

import model.*;

import java.util.ArrayList;

public interface GameDAO {
    void clear() throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    void removeGame(Integer gameID) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    int length() throws DataAccessException;
}
