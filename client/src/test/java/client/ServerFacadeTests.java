package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import dataaccess.SqlUserDAO;
import model.GameInfo;
import org.junit.jupiter.api.*;
import requestResponse.ChessRequest;
import requestResponse.ServerResponse;
import server.Server;
import service.GameService;
import service.UserService;
import ui.ServerFacade;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static SqlUserDAO sqlUserDAO;
    private static SqlAuthDAO sqlAuthDAO;
    private static SqlGameDAO sqlGameDAO;
    private static UserService userService;
    private static GameService gameService;

    @BeforeAll
    public static void init() {
        server = new Server();
        sqlUserDAO = new SqlUserDAO();
        sqlAuthDAO = new SqlAuthDAO();
        sqlGameDAO = new SqlGameDAO();
        userService = new UserService(sqlUserDAO, sqlAuthDAO);
        gameService = new GameService(sqlGameDAO, sqlAuthDAO);

        facade = new ServerFacade("http://localhost:8080");
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void resetTest() throws DataAccessException {
        userService.clear();
        gameService.clear();
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

    @Test
    public void logoutTest() throws DataAccessException {
        ChessRequest registerRequest = new ChessRequest();
        registerRequest.setUsername("user");
        registerRequest.setPassword("password");
        registerRequest.setEmail("email.com");
        String auth = userService.register(registerRequest).getAuthToken();
        Assertions.assertEquals(1, sqlAuthDAO.length());
        ServerResponse response = facade.logout(auth);
        Assertions.assertEquals(0, sqlAuthDAO.length());
        Assertions.assertNotNull(response);
    }

    @Test
    public void logoutNegative() {
        // Bad Format
        assertThrows(RuntimeException.class, () -> facade.logout( null));
        // Wrong authToken
        assertThrows(RuntimeException.class, () -> facade.logout("123456789"));
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        // Register a user
        ChessRequest registerRequest = new ChessRequest();
        registerRequest.setUsername("user");
        registerRequest.setPassword("password");
        registerRequest.setEmail("email.com");
        String auth = userService.register(registerRequest).getAuthToken();

        // Create a game
        ChessRequest createRequest = new ChessRequest();
        createRequest.setGameName("testGame");
        createRequest.setAuthToken(auth);
        ServerResponse createResponse = gameService.createGame(createRequest);

        // Test the list Games function
        ServerResponse response = facade.listGames(auth);

        ArrayList<GameInfo> gameList = response.getGames();

        Assertions.assertEquals(1, gameList.size());
        GameInfo game = gameList.getFirst();

        Assertions.assertEquals("testGame", game.gameName());
        Assertions.assertNotNull(game.gameID());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertNull(game.whiteUsername());
    }
    @Test
    public void listGamesNegative() {
        // Wrong authToken
        assertThrows(RuntimeException.class, () -> facade.listGames("123456789"));
    }

    @Test
    public void createGameTest() throws DataAccessException {
        // Register a user
        ChessRequest registerRequest = new ChessRequest();
        registerRequest.setUsername("user");
        registerRequest.setPassword("password");
        registerRequest.setEmail("email.com");
        String auth = userService.register(registerRequest).getAuthToken();
        // Create a g
        // Test the createGame function
        ServerResponse response = facade.createGame("testGame", auth);
        Assertions.assertNotNull(response.getGameID());
    }
    @Test
    public void createGameNegative() {
        // Bad Format
        assertThrows(RuntimeException.class, () -> facade.createGame(null, null));
        // Wrong authToken
        assertThrows(RuntimeException.class, () -> facade.createGame("testGame", "123456789"));
    }

}
