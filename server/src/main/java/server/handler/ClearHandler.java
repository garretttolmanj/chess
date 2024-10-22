package server.handler;

import server.request.ChessRequest;
import server.response.ServerResponse;
import service.GameService;
import service.UserService;

public class ClearHandler extends Handler {
    private final UserService userService;
    private final GameService gameService;

    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) {
        gameService.clear();
        return userService.clear();
    }
}