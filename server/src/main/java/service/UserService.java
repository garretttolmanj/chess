package service;

import dataaccess.*;
import model.*;

public class UserService {
    public AuthData register(UserData user) {
        return null;
    }

    /**
     * Receives a userData object from the handler
     * Initializes a UserDAO
     * Calls the UserDAO, getUser method
     * Verify password,
     * create AuthData object
     * Initialize AuthDAO
     * Use AuthDAO Add AuthData
     * Creates a Result Object
     *
     * @param user
     * @return AuthData
     */
    public AuthData login(UserData user) {
        return null;
    }
    public void logout(AuthData auth) {}
}
