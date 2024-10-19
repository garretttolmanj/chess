package service;

import dataaccess.*;
import model.*;
import server.request.ChessRequest;
import server.response.ServerResponse;
import java.util.UUID;

public class GameService {
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameService(GameDAO gameAccess, AuthDAO authAccess) {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    // Clear the DAO's
//    public ServerResponse clear() {
//        gameAccess.clear();
//        authAccess.clear();
//        return new ServerResponse();
//    }

    // Return a list of games
    public ServerResponse listGames(ChessRequest listGamesRequest) {
        return new ServerResponse();
    }
}
