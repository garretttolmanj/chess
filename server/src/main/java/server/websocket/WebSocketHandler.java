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
        socketService.connect(session, username, command, connections);
    }

    private void makeMove(Session session, String username, UserGameCommand command) throws InvalidMoveException, IOException, DataAccessException {
        socketService.makeMove(session, username, command, connections);
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        socketService.leave(session, username, command, connections);
    }

    private void resign(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        socketService.resign(session, username, command, connections);
    }

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        remote.sendString(new Gson().toJson(message));
    }

}
