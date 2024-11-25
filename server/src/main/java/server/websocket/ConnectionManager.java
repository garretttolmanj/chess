package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // Map of gameID to another map of username to connection
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> gameConnections = new ConcurrentHashMap<>();

    // Add a connection for a specific game
    public void add(Integer gameID, String username, Session session) {
        gameConnections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        var connection = new Connection(username, session);
        gameConnections.get(gameID).put(username, connection);
    }

    // Remove a connection for a specific game
    public void remove(Integer gameID, String username) {
        var connections = gameConnections.get(gameID);
        if (connections != null) {
            connections.remove(username);
            if (connections.isEmpty()) {
                gameConnections.remove(gameID); // Remove game if no players remain
            }
        }
    }

    // Broadcast a message to all players in a specific game
    public void broadcast(Integer gameID, String excludeUsername, ServerMessage notification) throws IOException {
        var connections = gameConnections.get(gameID);
        if (connections == null) return; // No players in this game
        var removeList = new ArrayList<String>(); // Keep track of closed connections
        for (var entry : connections.entrySet()) {
            var username = entry.getKey();
            var connection = entry.getValue();
            try {
                if (connection.session.isOpen()) {
                    if (!Objects.equals(username, excludeUsername)){
                        connection.send(new Gson().toJson(notification));
                    }
                } else {
                    removeList.add(username); // Add closed connections to remove list
                }
            } catch (IOException e) {
                System.err.println("Error sending message to " + username + ": " + e.getMessage());
                removeList.add(username); // Add problematic connection to remove list
            }
        }

        // Remove any connections that are no longer active
        for (var username : removeList) {
            connections.remove(username);
        }

        if (connections.isEmpty()) {
            gameConnections.remove(gameID); // Clean up empty game groups
        }
    }
}
