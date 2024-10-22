package server.handler;

import server.request.ChessRequest;
import server.response.ServerResponse;
import service.UserService;

public class LogoutHandler extends Handler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) {
        return userService.logout(request);
    }
}
