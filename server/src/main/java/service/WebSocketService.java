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
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage(ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    public void makeMove(Session session, String username, UserGameCommand command, ConnectionManager connections) throws DataAccessException, IOException {
        // get the game and make sure the user is authorized to make a move.
        Integer gameID = command.getGameID();
        GameData gameData = getGame(gameID);
        ChessGame chessGame = gameData.game();
        ChessMove chessMove = command.getChessMove();
        ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();
        if (teamTurn == ChessGame.TeamColor.WHITE) {
            if (!username.equals(gameData.whiteUsername())) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Invalid Move");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
        } else {
            if (!username.equals(gameData.blackUsername())) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Invalid Move");
                session.getRemote().sendString(new Gson().toJson(error));
                return;
            }
        }
        try {
            // Try making the move and updating the game
            chessGame.makeMove(chessMove);
            updateGame(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
            // broadcast load game to all clients in the game
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setChessGame(chessGame);
            connections.broadcast(gameID, "", loadGame);
            // broadcast notification of move to all other clients
            var message = String.format("%s moved their piece", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setChessMove(chessMove);
            notification.setMessage(message);
            connections.broadcast(gameID, username, notification);
        } catch (InvalidMoveException | IOException ex) {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage("Invalid Move");
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    public void updateGame(Integer gameID, String white, String black, String gameName, ChessGame chessGame) throws DataAccessException {
        GameData gameData = new GameData(gameID, white, black, gameName, chessGame);
        gameAccess.updateGame(gameData);
    }
}
