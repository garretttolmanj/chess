package server.websocket;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.UnauthorizedException;
import service.WebSocketService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.rmi.Remote;
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
            sendMessage(session.getRemote(), new Error("Error: Unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new Error("Error: " + ex.getMessage()));
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        return socketService.getUserName(authToken);
    }

    private void saveSession(Integer gameID, Session session) {}

    private void connect(Session session, String username, UserGameCommand command) throws IOException {
        System.out.println(connections);
        connections.add(username, session);
        var message = String.format("%s is in the game", username);
        // Add functionality to add a message to the ServerMessage class
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("user", notification);
    }

    private void makeMove(Session session, String username, UserGameCommand command) {}

    private void leaveGame(Session session, String username, UserGameCommand command) {}

    private void resign(Session session, String username, UserGameCommand command) {}

    private void sendMessage(RemoteEndpoint remote, Error error) {}

}
