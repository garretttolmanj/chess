package service;


import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import server.request.ChessRequest;
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

        var registerRequest = new ChessRequest();
        registerRequest.setUsername("garry");
        registerRequest.setPassword("johnson");
        registerRequest.setEmail("garrett@mail.com");

        var registerResponse = userService.register(registerRequest);

        assertEquals("garry", registerResponse.getUsername());
        assertNotEquals(UUID.randomUUID().toString(), registerResponse.getAuthToken());

        System.out.println(userService.getUserAccess());
        System.out.println(userService.getAuthAccess());

    }

    @Test
    void loginPositive() throws DataAccessException, ResponseException {
        var user = new ChessRequest();
        user.setUsername("garrett");
        user.setPassword("johnson");

        var loginResponse = userService.login(user);
        assertEquals("garrett", loginResponse.getUsername());
        assertNotEquals(UUID.randomUUID().toString(), loginResponse.getAuthToken());
    }

    @Test
    void loginNegative() throws DataAccessException, ResponseException {
        var request1 = new ChessRequest();
        request1.setUsername("garry");
        request1.setPassword("johnson");
        // Expect a DataAccessException for username not found
        assertThrows(DataAccessException.class, () -> userService.login(request1));

        var request2 = new ChessRequest();
        request2.setUsername("garrett");
        request2.setPassword("john");
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
