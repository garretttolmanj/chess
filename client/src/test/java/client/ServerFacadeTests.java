package client;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlUserDAO;
import org.junit.jupiter.api.*;
import requestResponse.ChessRequest;
import requestResponse.ServerResponse;
import model.*;
import server.Server;
import service.AlreadyTakenException;
import service.UserService;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static SqlUserDAO sqlUserDAO;
    private static SqlAuthDAO sqlAuthDAO;
    private static UserService userService;

    @BeforeAll
    public static void init() {
        server = new Server();
        sqlUserDAO = new SqlUserDAO();
        sqlAuthDAO = new SqlAuthDAO();
        userService = new UserService(sqlUserDAO, sqlAuthDAO);
        facade = new ServerFacade("http://localhost:8080");
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void resetTest() throws DataAccessException {
        userService.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTest() {
        ServerResponse response = facade.register("user", "password", "email.com");
        Assertions.assertEquals("user", response.getUsername());
        Assertions.assertNotNull(response.getAuthToken());
    }

    @Test
    public void registerNegative() {
        // Bad Format
        assertThrows(RuntimeException.class, () -> facade.register("user", null, null));
        // Already Taken
        facade.register("user", "password", "email.com");
        assertThrows(RuntimeException.class, () -> facade.register("user", "password2", "email2.com"));
    }

    @Test
    public void loginTest() throws DataAccessException {
        ChessRequest registerRequest = new ChessRequest();
        registerRequest.setUsername("user");
        registerRequest.setPassword("password");
        registerRequest.setEmail("email.com");
        userService.register(registerRequest);
//        UserData newUser = new UserData("user", "password", "email.com");
//        sqlUserDAO.createUser(newUser);
        ServerResponse response = facade.login("user", "password");
        Assertions.assertEquals("user", response.getUsername());
        Assertions.assertNotNull(response.getAuthToken());
    }

    @Test
    public void loginNegative() {
        // Bad Format
        assertThrows(RuntimeException.class, () -> facade.login("user", null));
        // Wrong Password
        assertThrows(RuntimeException.class, () -> facade.login("user", "wrong"));
    }
}
