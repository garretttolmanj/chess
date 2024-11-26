package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.websocket.ConnectionManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketService extends Service {
    // Initialize Data Access Objects
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;

    public WebSocketService(AuthDAO authAccess, GameDAO userAccess) {
        super(authAccess);
        this.authAccess = authAccess;
        this.gameAccess = userAccess;
    }

    public String getUserName(String authToken) throws DataAccessException {
        checkAuthToken(authToken);
        return authAccess.getAuth(authToken).username();
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        return gameAccess.getGame(gameID);
    }

    public void connect(Session session, String username, UserGameCommand command, ConnectionManager connections) throws IOException {
        try {
            // Add user to the game
            Integer gameID = command.getGameID();
            GameData gameData = getGame(gameID);
            ChessGame chessGame = gameData.game();
            connections.add(gameID, username, session);
            // Send LOAD GAME to the user
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setChessGame(chessGame);
            session.getRemote().sendString(new Gson().toJson(loadGame));

            // Broadcast connection to other users
            var message = String.format("%s is in the game", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(message);
            connections.broadcast(gameID, username, notification);
        } catch (DataAccessException | IOException ex) {
            sendErrorMessage(session,ex.getMessage());
        }
    }

    public void resign(Session session, String username, UserGameCommand command, ConnectionManager connections) throws IOException, DataAccessException {
        Integer gameID = command.getGameID();
        GameData gameData;

        try {
            gameData = getGame(gameID);
        } catch (DataAccessException e) {
            sendErrorMessage(session, "The Game is already over.");
            return;
        }

        // Validate the player and game state
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            sendErrorMessage(session, "Invalid Resignation");
            return;
        }

        gameAccess.removeGame(gameID);

        // Broadcast resignation to all users
        var message = String.format("%s has resigned, the game is over.", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(gameID, "", notification);
    }


    public void makeMove(Session session, String username, UserGameCommand command, ConnectionManager connections) throws DataAccessException, IOException {
        Integer gameID = command.getGameID();
        GameData gameData;
        try {
            gameData = getGame(gameID);
        } catch (DataAccessException e) {
            sendErrorMessage(session, "The Game is already over.");
            return;
        }
        ChessGame chessGame = gameData.game();
        ChessMove chessMove = command.getChessMove();
        ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();
        try {
            // Validate the player and game state
            if (isUnauthorized(chessGame, username, gameData, session)) {
                return;
            };
            if (isGameOver(chessGame, session)) {
                return;
            };

            // Make the move and update the game
            chessGame.makeMove(chessMove);
            updateGame(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);

            // Broadcast the updated game state
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setChessGame(chessGame);
            connections.broadcast(gameID, "", loadGame);

            // Handle game state conditions (check, checkmate, stalemate)
            if (handleGameConditions(chessGame, chessMove, teamTurn, connections, gameID)) {
                return;
            }

            // Notify other clients of the move
            broadcastMoveNotification(chessMove, username, connections, gameID);

        } catch (InvalidMoveException | DataAccessException | IOException ex) {
            sendErrorMessage(session, "Invalid Move: " + ex.getMessage());
        }
    }

    private boolean isUnauthorized(ChessGame chessGame, String username, GameData gameData, Session session) throws IOException {
        ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();
        String expectedPlayer = (teamTurn == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        if (!username.equals(expectedPlayer)) {
            sendErrorMessage(session, "It's not your turn!");
            return true;
        }
        return false;
    }

    private boolean isGameOver(ChessGame chessGame, Session session) throws IOException {
        if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) || chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                chessGame.isInStalemate(ChessGame.TeamColor.WHITE) || chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            sendErrorMessage(session, "Game is already over.");
            return true;
        }
        return false;
    }

    private boolean handleGameConditions(ChessGame chessGame, ChessMove chessMove, ChessGame.TeamColor teamTurn, ConnectionManager connections, Integer gameID) throws IOException {
        ChessGame.TeamColor opponentTeam = (teamTurn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        System.out.println("Inside handle Game Conditions. Opponent: " + opponentTeam);
        if (chessGame.isInCheck(opponentTeam)) {
            System.out.println("Inside is in Check");
            gameStateNotification(String.format("%s is in check", opponentTeam), chessMove, connections, gameID);
            return false;
        } else if (chessGame.isInCheckmate(opponentTeam)) {
            gameStateNotification(String.format("Checkmate!! %s wins!", teamTurn), chessMove, connections, gameID);
            return true;
        } else if (chessGame.isInStalemate(opponentTeam)) {
            gameStateNotification("Stalemate!!!", chessMove, connections, gameID);
            return true;
        }
        return false;
    }

    private void broadcastMoveNotification(ChessMove chessMove, String username, ConnectionManager connections, Integer gameID) throws IOException {
        var message = String.format("%s moved their piece", username);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setChessMove(chessMove);
        notification.setMessage(message);
        connections.broadcast(gameID, username, notification);
    }

    private void sendErrorMessage(Session session, String errorMessage) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(errorMessage);
        session.getRemote().sendString(new Gson().toJson(error));
    }

    private void gameStateNotification(String message, ChessMove chessMove, ConnectionManager connections, Integer gameID) throws IOException {
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setChessMove(chessMove);
        notification.setMessage(message);
        connections.broadcast(gameID, "", notification);
    }

    public void updateGame(Integer gameID, String white, String black, String gameName, ChessGame chessGame) throws DataAccessException {
        GameData gameData = new GameData(gameID, white, black, gameName, chessGame);
        gameAccess.updateGame(gameData);
    }
}
