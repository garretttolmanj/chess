package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.*;

public class GameClient implements Client{
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private final String authToken;
    private final Integer gameID;
    private final String teamColor;
    private WebSocketFacade ws;
    private ChessGame chessGame;
    private final BoardRenderer boardRenderer = new BoardRenderer();

    public GameClient(String serverUrl, NotificationHandler notificationHandler, String authToken, Integer gameID, String teamColor) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
        try {
            this.connect();
        } catch (RuntimeException e) {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage(e.getMessage());
            notificationHandler.notify(error);
            notificationHandler.signIn(authToken);
        }
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "drawboard" -> drawBoard();
                case "move" -> move(params);
                case "resign" -> resign();
                case "leave" -> leave();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private void connect() {
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.enterGame(authToken, gameID);
    }

    private boolean awaitingConfirmation = false; // Tracks whether the client is awaiting confirmation for resignation.

    private String resign() {
        if (!awaitingConfirmation) {
            awaitingConfirmation = true;
            return "Are you sure you want to resign? Type 'resign' again to confirm.";
        } else {
            awaitingConfirmation = false; // Reset the flag after confirmation.
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.resignGame(authToken, gameID);
            return "You have resigned from the game.";
        }
    }


    public void loadGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public String drawBoard() {
        if (chessGame == null) {
            return "No game loaded";
        }
        boolean rotateBoard;
        if (teamColor == null) {
            rotateBoard = true;
            return "\n" + boardRenderer.renderBoard(chessGame, rotateBoard);
        } else {
            rotateBoard = !teamColor.equals("BLACK");
            return "\n" + boardRenderer.renderBoard(chessGame, rotateBoard);
        }

    }

    public String move(String... params) {
        if (params.length == 2) {
            try {
                ChessPosition startPosition = getCoordinates(params[0]);
                ChessPosition endPosition = getCoordinates(params[1]);
                ChessMove move = new ChessMove(startPosition, endPosition, null);
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.makeMove(authToken, gameID, move);
                return "";
            } catch (RuntimeException e) {
                throw new RuntimeException("Expected: move <startPosition> <endPosition>");
            }
        }
        throw new RuntimeException("Expected: move <startPosition> <endPosition>");
    }

    private final ArrayList<String> alphabet = new ArrayList<>(List.of("a", "b", "c", "d", "e", "f", "g", "h"));

    private ChessPosition getCoordinates(String square) {
        String letter = square.substring(0, 1);
        String number = square.substring(1);
        if (!alphabet.contains(letter)) {
            throw new RuntimeException("Invalid input");
        }
        int row = alphabet.indexOf(letter) + 1;
        int col = Integer.parseInt(number);
        if (col < 1 || col > 8) {
            throw new RuntimeException("Invalid input");
        }
        return new ChessPosition(col, row);
    }

    public String leave() {
        notificationHandler.signIn(authToken);
        return "Left Game";
    }

    public String help() {
        return """
                - help
                - drawBoard
                - move <startPosition> <endPosition>
                - leave
                """;
    }
}
