package server.handler;

import dataaccess.DataAccessException;
import requestResponse.*;
import service.UserService;

public class LogoutHandler extends Handler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ServerResponse handleRequest(ChessRequest request) throws DataAccessException {
        return userService.logout(request);
    }
}
