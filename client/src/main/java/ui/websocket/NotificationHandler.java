package ui.websocket;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
    void signIn(String authToken);
}
