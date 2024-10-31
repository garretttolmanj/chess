package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import server.request.ChessRequest;
import server.response.ServerResponse;

import java.util.ArrayList;
import java.util.Random;


public class GameService extends Service {
    // Initialize the Data Access objects
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameService(GameDAO gameAccess, AuthDAO authAccess) {
        super(authAccess);
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    // Clears all the data in the database.
    public ServerResponse clear() throws DataAccessException {
        gameAccess.clear();
        return new ServerResponse();
    }

    // Return a list of games
    public ServerResponse listGames(ChessRequest listGamesRequest) throws DataAccessException {
        String authToken = listGamesRequest.getAuthToken();
        checkAuthToken(authToken);
        ArrayList<GameData> games = gameAccess.listGames();
        return createListGamesResponse(games);
    }

    // LIST GAMES HELPER FUNCTION. Collect the necessary game info and return a Response with a list of Games.
    private ServerResponse createListGamesResponse(ArrayList<GameData> games) {
        ArrayList<GameInfo> gameList = new ArrayList<>();
        for (GameData game : games) {
            gameList.add(new GameInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        ServerResponse listGamesResponse = new ServerResponse();
        listGamesResponse.setGames(gameList);
        return listGamesResponse;
    }

    // Creates and new game.
    public ServerResponse createGame(ChessRequest createGameRequest) throws DataAccessException {
        String gameName = createGameRequest.getGameName();
        // Verify that the request is valid
        if (gameName == null) {
            throw new BadRequestException("Error: bad request");
        }
        // Verify the authToken
        String authToken = createGameRequest.getAuthToken();
        checkAuthToken(authToken);
        // Create a newGame
        int gameID;
        do {
            gameID = new Random().nextInt(1, 1000000);
        } while (isGameIDTaken(gameID));

        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameAccess.createGame(newGame);
        ServerResponse createGameResponse = new ServerResponse();
        createGameResponse.setGameID(gameID);
        return createGameResponse;
    }

    // CREATE GAME HELPER FUNCTION. Checks to see if a gameID has been taken to ensure that no 2 games have the same ID.
    private boolean isGameIDTaken(Integer gameID) throws DataAccessException {
        for (GameData game : gameAccess.listGames()) {
            if (game.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    // Adds a user as the white or black player in an existing game.
    public ServerResponse joinGame(ChessRequest joinGameRequest) throws DataAccessException {
        // Verify the authToken
        String authToken = joinGameRequest.getAuthToken();
        checkAuthToken(authToken);
        String username = authAccess.getAuth(authToken).username();
        // Verify that the request is valid
        String playerColor = joinGameRequest.getPlayerColor();
        Integer gameID = joinGameRequest.getGameID();
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new BadRequestException("Error: bad request");
        }
        if (gameID == null) {
            throw new BadRequestException("Error: bad request");
        }
        GameData game = gameAccess.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: game does not exist");
        }
        // Determine if the player color spot is available
        if (isSpotOpen(game, playerColor)) {
            // Update the game based on the player color
            GameData updatedGame = addPlayer(game, playerColor, username);
            gameAccess.removeGame(gameID);
            gameAccess.createGame(updatedGame);
            return new ServerResponse();
        } else {
            throw new AlreadyTakenException("Error: already taken");
        }
    }

    // JOIN GAME HELPER FUNCTION. Checks to see if a white or black player spot is open.
    private boolean isSpotOpen(GameData game, String playerColor) {
        if (playerColor.equals("WHITE")) {
            return game.whiteUsername() == null;
        } else if (playerColor.equals("BLACK")) {
            return game.blackUsername() == null;
        }
        return false;
    }

    // JOIN GAME HELPER FUNCTION. Adds a player to a new GameData object.
    private GameData addPlayer(GameData game, String playerColor, String username) {
        if (playerColor.equals("WHITE")) {
            return new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            return new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
    }
}
