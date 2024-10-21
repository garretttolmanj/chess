package service;


import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.ChessRequest;
import server.response.ResponseException;
import server.response.ServerResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I need to test the following in this TestClass
 * adding a User work correctly
 */
public class UserServiceTest {

    private static UserService userService;
    private static MemoryUserDAO userMemory;
    private static MemoryAuthDAO authMemory;
    private static MemoryUserDAO testUserMemory;
    private static MemoryAuthDAO testAuthMemory;
    private static String authToken;

    // This method will run once before all the tests
    @BeforeAll
    public static void setUp() {
        // Create a test user and the necessary DAOs
        UserData testUser = new UserData("garrett", "johnson", "garrett@email.com");

        userMemory = new MemoryUserDAO();
        authMemory = new MemoryAuthDAO();
        userMemory.createUser(testUser);

        userService = new UserService(userMemory, authMemory);
        // Initialize test Memory DAO's
        testUserMemory = new MemoryUserDAO();
        testAuthMemory = new MemoryAuthDAO();
    }
    @BeforeEach
    void resetTestMemory() {
        testUserMemory.clear();
        testAuthMemory.clear();
        UserData testUser1 = new UserData("garrett", "johnson", "garrett@email.com");
        testUserMemory.createUser(testUser1);

        userMemory.clear();
        authMemory.clear();
        userMemory.createUser(testUser1);

        authToken = null;
        assertNotNull(userMemory.getUser("garrett"));
    }

    @Test
    void registerPositive() throws DataAccessException {
        //update the test Memory
        UserData testUser2 = new UserData("user", "password", "user@mail.com");
        testUserMemory.createUser(testUser2);

        // Perform the register method
        var registerRequest = new ChessRequest();
        registerRequest.setUsername("user");
        registerRequest.setPassword("password");
        registerRequest.setEmail("user@mail.com");
        var registerResponse = userService.register(registerRequest);

        // Make sure the memory was updated correctly
        assertEquals(testUserMemory, userMemory);
        assertEquals(1, authMemory.length());

        // Check to see if the service response is what is expected
        assertEquals("user", registerResponse.getUsername());
        assertNotNull(registerResponse.getAuthToken());
        // R

    }
    @Test
    void registerNegative() {
        // Test UserName Taken
        var request1 = new ChessRequest();
        request1.setUsername("garrett");
        request1.setPassword("password");
        request1.setEmail("user@mail.com");
        assertThrows(AlreadyTakenException.class, () -> userService.register(request1));
        // Test Bad request
        var request2 = new ChessRequest();
        request2.setUsername("garrett");
        request2.setPassword("password");
        assertThrows(BadRequestException.class, () -> userService.register(request2));

        // Test to see if the memory DAO is still the same
        assertEquals(testUserMemory, userMemory);
        assertEquals(0, authMemory.length());

    }

    @Test
    void loginPositive() throws DataAccessException, ResponseException {
        // Test successful login
        var loginRequest = new ChessRequest();
        loginRequest.setUsername("garrett");
        loginRequest.setPassword("johnson");
        var loginResponse = userService.login(loginRequest);

        // Make sure DAO's are what's expected
        assertEquals(testUserMemory, userMemory);
        assertEquals(1, authMemory.length());

        // Make sure the response is as expected
        assertEquals("garrett", loginResponse.getUsername());
        assertNotNull(loginResponse.getAuthToken());
    }

    @Test
    void loginNegative() throws DataAccessException, ResponseException {
        // Test non-existent username
        var request1 = new ChessRequest();
        request1.setUsername("user");
        request1.setPassword("johnson");
        assertThrows(UnauthorizedException.class, () -> userService.login(request1));
        // Test wrong password
        var request2 = new ChessRequest();
        request2.setUsername("garrett");
        request2.setPassword("john");
        assertThrows(UnauthorizedException.class, () -> userService.login(request2));

        // Make sure DAO's are as expected
        assertEquals(testUserMemory, userMemory);
        assertEquals(0, authMemory.length());

    }

    @Test
    void logoutPositive() {
        // First log in existing user
        var loginRequest = new ChessRequest();
        loginRequest.setUsername("garrett");
        loginRequest.setPassword("johnson");
        authToken = userService.login(loginRequest).getAuthToken();
        // Make sure login was successful
        assertEquals(testUserMemory, userMemory);
        assertEquals(1, authMemory.length());

        // Test successful logout
        var logoutRequest = new ChessRequest();
        logoutRequest.setAuthToken(authToken);
        var logoutResponse = userService.logout(logoutRequest);
        assertEquals(testUserMemory, userMemory);
        assertEquals(0, authMemory.length());
        // Test response
        assertEquals(new ServerResponse(), logoutResponse);
    }

    @Test
    void logoutNegative() {
        // First log in existing user
        var loginRequest = new ChessRequest();
        loginRequest.setUsername("garrett");
        loginRequest.setPassword("johnson");
        userService.login(loginRequest);
        // Make sure login was successful
        assertEquals(testUserMemory, userMemory);
        assertEquals(1, authMemory.length());
        // Test bad authToken
        authToken = "1ijfqpeifjq";
        var logoutRequest = new ChessRequest();
        logoutRequest.setAuthToken(authToken);
        assertThrows(UnauthorizedException.class, () -> userService.logout(logoutRequest));
        // Make sure logout didn't occur
        assertEquals(testUserMemory, userMemory);
        assertEquals(1, authMemory.length());
    }

    @Test
    void clear() {
        // Clear the test DAOs
        testUserMemory.clear();
        testAuthMemory.clear();
        // test clear method
        var clearResponse = userService.clear();
        assertEquals(testUserMemory, userMemory);
        assertEquals(testAuthMemory, authMemory);
        // Check to make sure response is correct
        assertEquals(new ServerResponse(), clearResponse);
    }

}
