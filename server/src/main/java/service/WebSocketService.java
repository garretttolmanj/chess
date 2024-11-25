package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

public class WebSocketService extends Service {
    // Initialize Data Access Objects
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public WebSocketService(AuthDAO authAccess, GameDAO userAccess) {
        super(authAccess);
        this.authAccess = authAccess;
        this.gameAccess = userAccess;
    }

    public String getUserName(String authToken) throws DataAccessException {
        checkAuthToken(authToken);
        return authAccess.getAuth(authToken).username();
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        GameData gameData = gameAccess.getGame(gameID);
        return gameData;
    }

    public void updateGame(Integer gameID, String white, String black, String gameName, ChessGame chessGame) throws DataAccessException {
        GameData gameData = new GameData(gameID, white, black, gameName, chessGame);
        gameAccess.updateGame(gameData);
    }
}
