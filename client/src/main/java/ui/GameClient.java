package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Map;

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

    private static final Map<ChessPiece.PieceType, String> WHITE_PIECES = Map.of(
            ChessPiece.PieceType.KING, WHITE_KING,
            ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
            ChessPiece.PieceType.ROOK, WHITE_ROOK,
            ChessPiece.PieceType.BISHOP, WHITE_BISHOP,
            ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
            ChessPiece.PieceType.PAWN, WHITE_PAWN
    );
    private static final Map<ChessPiece.PieceType, String> BLACK_PIECES = Map.of(
            ChessPiece.PieceType.KING, BLACK_KING,
            ChessPiece.PieceType.QUEEN, BLACK_QUEEN,
            ChessPiece.PieceType.ROOK, BLACK_ROOK,
            ChessPiece.PieceType.BISHOP, BLACK_BISHOP,
            ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
            ChessPiece.PieceType.PAWN, BLACK_PAWN
    );
    private String[] alphabet = new String[] {"a", " b ", "c", "d", " e", " f", "g", "h"};

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return "";
        }
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? WHITE_PIECES.get(piece.getPieceType())
                : BLACK_PIECES.get(piece.getPieceType());
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


    public String leave() {
        notificationHandler.signIn(authToken);
        return "Left Game";
    }

    public String help() {
        return """
                - help
                - drawBoard
                - leave
                """;
    }
}
