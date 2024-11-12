package client;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlUserDAO;
import org.junit.jupiter.api.*;
import requestResponse.ServerResponse;
import server.Server;
import service.AlreadyTakenException;
import service.UserService;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        facade = new ServerFacade("http://localhost:8080");
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void resetTest() throws DataAccessException {
        SqlUserDAO sqlUserDAO = new SqlUserDAO();
        SqlAuthDAO sqlAuthDAO = new SqlAuthDAO();
        UserService userService = new UserService(sqlUserDAO, sqlAuthDAO);
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

}
