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
//    void clear() throws Exception {
//    }

    @Test
    void loginPositive() throws DataAccessException, ResponseException {
        var user = new UserData("garrett", "johnson", "garrett@email.com");

        var loginResponse = userService.login(user);
        LoginResponse correctAuthData = new LoginResponse(UUID.randomUUID().toString(), "garrett");
        assertEquals("garrett", loginResponse.getUsername());
        assertEquals(correctAuthData, loginResponse);


//      length of users should be one
//      It should be equal to the following object
//      It should be equal to the following UserData
//        assertEquals(1, )
//        assertEquals()
    }
    @Test
    void addUserNegative() throws ResponseException {
    }


}
