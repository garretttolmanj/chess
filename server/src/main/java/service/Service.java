package service;

import dataaccess.AuthDAO;
import model.AuthData;

// Service Base Class. Pretty much just holds the authToken checking logic.
public abstract class Service {
    private final AuthDAO authAccess;

    public Service(AuthDAO authAccess) {
        this.authAccess = authAccess;
    }

    public void checkAuthToken(String authToken) {
        AuthData auth = authAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
