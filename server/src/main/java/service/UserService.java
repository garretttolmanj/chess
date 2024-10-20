package service;

import dataaccess.*;
import model.*;
import server.request.ChessRequest;
import server.response.ServerResponse;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public ServerResponse clear() {
        userAccess.clear();
        authAccess.clear();
        return new ServerResponse();
    }

    public ServerResponse register(ChessRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData userData = new UserData(username, password, email);

        if (userAccess.getUser(username) == null) {
            userAccess.createUser(userData);

            String token = UUID.randomUUID().toString();
            AuthData authData = new AuthData(token, username);
            authAccess.createAuth(authData);

            ServerResponse registerResponse = new ServerResponse();
            registerResponse.setUsername(registerRequest.getUsername());
            registerResponse.setAuthToken(token);
            return registerResponse;
        } else {
            throw new AlreadyTakenException("Error: already taken");
        }
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
     * @param loginRequest
     * @return AuthData
     */
    public ServerResponse login(ChessRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if (username == null || password == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData userData = userAccess.getUser(username);

        if (userData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (userData.password().equals(password)) {
            String token = UUID.randomUUID().toString();
            AuthData authData = new AuthData(token, username);
            authAccess.createAuth(authData);

            ServerResponse loginResponse = new ServerResponse();
            loginResponse.setUsername(username);
            loginResponse.setAuthToken(token);
            return loginResponse;
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }


    }
    public ServerResponse logout(ChessRequest request) {
        String authToken = request.getAuthToken();
        AuthData auth = authAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            authAccess.deleteAuth(authToken);
            return new ServerResponse();
        }
    }

}
