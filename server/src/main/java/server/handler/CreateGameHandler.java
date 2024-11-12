package server.handler;

import dataaccess.DataAccessException;
import requestResponse.*;
import service.GameService;

public class CreateGameHandler extends Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) throws DataAccessException {
        return gameService.createGame(request);
    }
}
