package server.handler;

import server.request.ChessRequest;
import server.response.ServerResponse;
import service.UserService;

public class LoginHandler extends Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) {
        return userService.login(request);
    }
}

