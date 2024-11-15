package server.handler;

import dataaccess.DataAccessException;
import requestresponse.*;
import service.GameService;

public class JoinGameHandler extends Handler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) throws DataAccessException {
        return gameService.joinGame(request);
    }
}
