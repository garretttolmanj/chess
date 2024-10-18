package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.response.LoginResponse;
import server.response.ResponseException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I need to test the following in this TestClass
 * adding a User work correctly
 */
public class UserServiceTest {
    static final UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());


//    @BeforeEach
//    void clear() throws DataAccessException {
//        userService.clear();
//    }

    @Test
    void registerPositive() throws DataAccessException {
        System.out.println(userService.getUserAccess());
        System.out.println(userService.getAuthAccess());

        var registerRequest = new RegisterRequest("garry", "johnson", "garrett@mail.com");
        var registerResponse = userService.register(registerRequest);

        assertEquals("garry", registerResponse.getUsername());
        assertNotEquals(UUID.randomUUID().toString(), registerResponse.getAuthToken());

        System.out.println(userService.getUserAccess());
        System.out.println(userService.getAuthAccess());

    }

    @Test
    void loginPositive() throws DataAccessException, ResponseException {
        var user = new LoginRequest("garrett", "johnson");
        var loginResponse = userService.login(user);
        assertEquals("garrett", loginResponse.getUsername());
        assertNotEquals(UUID.randomUUID().toString(), loginResponse.getAuthToken());
    }

    @Test
    void loginNegative() throws DataAccessException, ResponseException {
        var request1 = new LoginRequest("garry", "johnson");

        // Expect a DataAccessException for username not found
        assertThrows(DataAccessException.class, () -> userService.login(request1));

        var request2 = new LoginRequest("garrett", "john");

        // Expect a ResponseException for password mismatch
        assertThrows(ResponseException.class, () -> userService.login(request2));
    }

    @Test
    void clear() {
        System.out.println(userService.getUserAccess());
        System.out.println(userService.getAuthAccess());
        userService.clear();

        System.out.println(userService.getUserAccess());
    }

}
