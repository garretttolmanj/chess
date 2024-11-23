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

    public ChessGame getGame(Integer gameID) throws DataAccessException {
        GameData gameData = gameAccess.getGame(gameID);
        return gameData.game();
    }
}
