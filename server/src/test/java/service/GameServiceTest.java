package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.ChessRequest;
import server.response.ServerResponse;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private static UserService userService;
    private static GameService gameService;
    private static MemoryUserDAO userMemory;
    private static MemoryGameDAO gameMemory;
    private static MemoryAuthDAO authMemory;
    private static MemoryGameDAO testGameMemory;
    private static MemoryAuthDAO testAuthMemory;
    private static String authToken;

    @BeforeAll
    public static void setup() {
        // Sign in a user and get an authToken
        authMemory = new MemoryAuthDAO();
        authToken = UUID.randomUUID().toString();
        authMemory.createAuth(new AuthData(authToken, "garrett"));
        // Initialize the gameDAO and the service
        gameMemory = new MemoryGameDAO();
        gameService = new GameService(gameMemory, authMemory);
        // Initialize the test DAO's
        testGameMemory = new MemoryGameDAO();
        testAuthMemory = new MemoryAuthDAO();
        testAuthMemory.createAuth(new AuthData(authToken, "garrett"));
    }

    @BeforeEach
    void resetTestMemory() {
        testGameMemory.clear();
        gameMemory.clear();
        GameData testGame1 = new GameData(1234,
                null,
                null,
                "Test Game",
                new ChessGame());

        testGameMemory.createGame(testGame1);
        gameMemory.createGame(testGame1);
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        // Get game list
        ChessRequest listGamesRequest = new ChessRequest();
        listGamesRequest.setAuthToken(authToken);
        ServerResponse listGamesResponse = gameService.listGames(listGamesRequest);
        // Ensure the memory remains unaltered
        assertEquals(testGameMemory, gameMemory);
        assertEquals(testAuthMemory, authMemory);

        // Ensure the response is what is expected
        ArrayList<GameInfo> expectedGames = new ArrayList<>();
        GameInfo expectedInfo = new GameInfo(1234, null, null, "Test Game");
        expectedGames.add(expectedInfo);
        ServerResponse expectedResponse = new ServerResponse();
        expectedResponse.setGames(expectedGames);

        assertEquals(expectedResponse, listGamesResponse);

    }

    @Test
    void listGamesNegative() {
        // Try a bad authToken
        ChessRequest listGamesRequests = new ChessRequest();
        listGamesRequests.setAuthToken("3aef6a15efaf6");
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(listGamesRequests));
        // Ensure the memory remains unaltered
        assertEquals(testGameMemory, gameMemory);
        assertEquals(testAuthMemory, authMemory);
    }

    @Test
    void createGamePositive() throws DataAccessException {
        // update test DAO's
        GameData testGame2 = new GameData(123455,
                null,
                null,
                "Test Game 2",
                new ChessGame());
        testGameMemory.createGame(testGame2);
        // Test gameService createGame
        ChessRequest createGameRequest = new ChessRequest();
        createGameRequest.setGameName("Test Game 2");
        createGameRequest.setAuthToken(authToken);
        ServerResponse createGameResponse = gameService.createGame(createGameRequest);

        // Ensure the memory was updated correctly
        assertEquals(2, gameMemory.length());
        assertEquals(testAuthMemory, authMemory);
        // Ensure the response matches the expected
        assertNotNull(createGameResponse.getGameID());
        // Remove the newGame for the other tests
        gameMemory.removeGame(createGameResponse.getGameID());
    }

    @Test
    void createGameNegative() {
        // Test bad authToken
        ChessRequest createGameRequest = new ChessRequest();
        createGameRequest.setGameName("Test Game 2");
        createGameRequest.setAuthToken("af14314eoij014u1");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(createGameRequest));
        // Test bad Request
        ChessRequest createGameRequest2 = new ChessRequest();
        createGameRequest2.setAuthToken(authToken);
        assertThrows(BadRequestException.class, () -> gameService.createGame(createGameRequest2));
        // Ensure the DAO's aren't altered
        assertEquals(testGameMemory, gameMemory);
        assertEquals(testAuthMemory, authMemory);
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        // update test DAO's
        testGameMemory.removeGame(1234);
        GameData testGame = new GameData(1234,
                "garrett",
                null,
                "Test Game",
                new ChessGame());
        testGameMemory.createGame(testGame);
        // Test gameService join Game, "WHITE"
        ChessRequest joinGameRequest = new ChessRequest();
        joinGameRequest.setPlayerColor("WHITE");
        joinGameRequest.setGameID(1234);
        joinGameRequest.setAuthToken(authToken);
        ServerResponse joinGameResponse = gameService.joinGame(joinGameRequest);
        // Test to make sure Data was updated correctly
        assertEquals(testGameMemory, gameMemory);
        assertEquals(testAuthMemory, authMemory);
        // Make sure the response is what is expected
        assertEquals(new ServerResponse(), joinGameResponse);
    }

    @Test
    void joinGameNegative() {
        // take a spot in the game
        testGameMemory.removeGame(1234);
        gameMemory.removeGame(1234);
        GameData testGame = new GameData(1234,
                "user",
                null,
                "Test Game",
                new ChessGame());
        gameMemory.createGame(testGame);
        testGameMemory.createGame(testGame);
        // Test bad authToken
        ChessRequest joinGameRequest = new ChessRequest();
        joinGameRequest.setPlayerColor("WHITE");
        joinGameRequest.setGameID(1234);
        joinGameRequest.setAuthToken("af14314eoij014u1");
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(joinGameRequest));
        // Test bad Request
        ChessRequest joinGameRequest2 = new ChessRequest();
        joinGameRequest2.setPlayerColor("RED");
        joinGameRequest2.setGameID(1234);
        joinGameRequest2.setAuthToken(authToken);
        assertThrows(BadRequestException.class, () -> gameService.joinGame(joinGameRequest2));
        // Test bad Request
        ChessRequest joinGameRequest3 = new ChessRequest();
        joinGameRequest2.setPlayerColor("WHITE");
        joinGameRequest2.setGameID(1234);
        joinGameRequest2.setAuthToken(authToken);
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(joinGameRequest2));
        // Ensure the DAO's aren't altered
        assertEquals(testGameMemory, gameMemory);
        assertEquals(testAuthMemory, authMemory);
    }

    @Test
    void clear() {
        // Clear the test DAOs
        testGameMemory.clear();
        // test clear method
        var clearResponse = gameService.clear();
        assertEquals(testGameMemory, gameMemory);
        // Check to make sure response is correct
        assertEquals(new ServerResponse(), clearResponse);
    }
}
