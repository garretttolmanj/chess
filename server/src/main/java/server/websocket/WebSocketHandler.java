package server.websocket;


import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import passoff.websocket.TestCommand;
import service.UnauthorizedException;
import service.WebSocketService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.rmi.Remote;
import java.util.Collection;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final WebSocketService socketService;

    public WebSocketHandler(WebSocketService socketService) {
        this.socketService = socketService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (UnauthorizedException ex) {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage("Error: Unauthorized");
            sendMessage(session.getRemote(), error);
        } catch (Exception ex) {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage(ex.getMessage());
            sendMessage(session.getRemote(), error);
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        return socketService.getUserName(authToken);
    }

    private void saveSession(Integer gameID, Session session) {}

    private void connect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        try {
            // Add user to the game
            Integer gameID = command.getGameID();
            GameData gameData = socketService.getGame(gameID);
            ChessGame chessGame = gameData.game();
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setChessGame(chessGame);
            sendMessage(session.getRemote(), loadGame);
            connections.add(gameID, username, session);

            // Broadcast connection to other users
            var message = String.format("%s is in the game", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setMessage(message);
            connections.broadcast(gameID, username, notification);
        } catch (DataAccessException ex) {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage(ex.getMessage());
            sendMessage(session.getRemote(), error);
        }

    }

    private void makeMove(Session session, String username, UserGameCommand command) throws DataAccessException, InvalidMoveException, IOException {
        // get the game
        Integer gameID = command.getGameID();
        GameData gameData = socketService.getGame(gameID);
        ChessGame chessGame = gameData.game();
        ChessMove chessMove = command.getChessMove();
        ChessPosition startPosition = chessMove.getStartPosition();
        try {
            // Try making the move and updating the game
            chessGame.makeMove(chessMove);
            socketService.updateGame(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame);
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
        } catch (InvalidMoveException ex) {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage("Invalid Move");
            sendMessage(session.getRemote(), error);
        }

    }

    private void leaveGame(Session session, String username, UserGameCommand command) {}

    private void resign(Session session, String username, UserGameCommand command) {}

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        remote.sendString(new Gson().toJson(message));
    }

}
