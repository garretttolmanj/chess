package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    ArrayList<GameData> listGames();
    void createGame(GameData game);
//    createGame: Create a new game.
//    getGame: Retrieve a specified game with the given game ID.
//    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
}
