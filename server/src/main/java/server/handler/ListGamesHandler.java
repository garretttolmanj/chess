package server.handler;

import dataaccess.DataAccessException;
import server.request.ChessRequest;
import server.response.ServerResponse;
import service.GameService;

public class ListGamesHandler extends Handler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) throws DataAccessException {
        return gameService.listGames(request);
    }
}


