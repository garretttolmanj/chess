package server.handler;

import server.request.ChessRequest;
import server.response.ServerResponse;
import service.GameService;

public class CreateGameHandler extends Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) {
        return gameService.createGame(request);
    }
}
