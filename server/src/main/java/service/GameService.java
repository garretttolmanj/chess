package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import server.request.ChessRequest;
import server.response.ServerResponse;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class GameService {
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameService(GameDAO gameAccess, AuthDAO authAccess) {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    // Clear the DAO's
    public ServerResponse clear() {
        gameAccess.clear();
        return new ServerResponse();
    }

    // Return a list of games
    public ServerResponse listGames(ChessRequest listGamesRequest) {
        String authToken = listGamesRequest.getAuthToken();
        AuthData auth = authAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else {
            ArrayList<GameData> games = gameAccess.listGames();
            return createListGamesResponse(games);
        }
    }

    // Collect the necessary game info and return a Response with a list of Games
    private ServerResponse createListGamesResponse(ArrayList<GameData> games) {
        ArrayList<GameInfo> gameList = new ArrayList<>();
        for (GameData game: games) {
            gameList.add(new GameInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        ServerResponse listGamesResponse = new ServerResponse();
        listGamesResponse.setGames(gameList);
        return listGamesResponse;
    }

    public ServerResponse createGame(ChessRequest createGameRequest) {
        String gameName = createGameRequest.getGameName();
        // Verify that the request is valid
        if (gameName == null) {
            throw new BadRequestException("Error: bad request");
        }
        // Verify the authToken
        String authToken = createGameRequest.getAuthToken();
        AuthData auth = authAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }else {
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
    }

    private boolean isGameIDTaken(Integer gameID) {
        for (GameData game : gameAccess.listGames()) {
            if (game.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    public ServerResponse joinGame(ChessRequest joinGameRequest) {
        // Verify the authToken
        String authToken = joinGameRequest.getAuthToken();
        AuthData auth = authAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
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
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() == null) {
                // Update the Game
                gameAccess.removeGame(gameID);
                GameData updatedGame = new GameData(gameID,
                        auth.username(),
                        game.blackUsername(),
                        game.gameName(),
                        game.game());
                gameAccess.createGame(updatedGame);
                return new ServerResponse();
            }
        }
        if (playerColor.equals("BLACK")) {
            if (game.blackUsername() == null) {
                // Update the Game
                gameAccess.removeGame(gameID);
                GameData updatedGame = new GameData(gameID,
                        game.whiteUsername(),
                        auth.username(),
                        game.gameName(),
                        game.game());
                gameAccess.createGame(updatedGame);
                return new ServerResponse();
            }
        }
        throw new AlreadyTakenException("Error: already taken");
    }
}
