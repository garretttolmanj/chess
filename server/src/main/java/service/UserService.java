package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.request.ChessRequest;
import server.response.ServerResponse;

import java.util.UUID;

public class UserService extends Service {

    // Initialize Data Access Objects
    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        super(authAccess);
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    // Clears all the data stored in the database.
    public ServerResponse clear() {
        userAccess.clear();
        authAccess.clear();
        return new ServerResponse();
    }

    // Registers a new user
    public ServerResponse register(ChessRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: bad request");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        UserData userData = new UserData(username, hashedPassword, email);

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

    // Logs in a user by creating a new session
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

        if (BCrypt.checkpw(password, userData.password())) {
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

    // Logs out a user by deleting the session.
    public ServerResponse logout(ChessRequest request) {
        String authToken = request.getAuthToken();
        checkAuthToken(authToken);
        authAccess.deleteAuth(authToken);
        return new ServerResponse();
    }

}
