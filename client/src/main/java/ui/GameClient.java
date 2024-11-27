package ui;

import chess.*;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;
import java.util.Scanner;
import java.util.*;

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
                case "show" -> show(params);
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

    public String leave() {
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.leaveGame(authToken, gameID);
        notificationHandler.signIn(authToken);
        return "Left Game";
    }

    public String show(String... params) {
        if (params.length == 1) {
            ChessPosition startPosition = getCoordinates(params[0]);
            ArrayList<ChessPosition> validMoves = new ArrayList<>();
            if (chessGame.validMoves(startPosition) == null) {
                return "No piece at that position";
            }
            else {
                for (ChessMove move : chessGame.validMoves(startPosition)) {
                    validMoves.add(move.getEndPosition());
                }
                boolean rotateBoard;
                if (teamColor == null) {
                    rotateBoard = true;
                    return "\n" + boardRenderer.renderBoard(chessGame, rotateBoard, validMoves);
                } else {
                    rotateBoard = !teamColor.equals("BLACK");
                    return "\n" + boardRenderer.renderBoard(chessGame, rotateBoard, validMoves);
                }
            }
        }
        return "Expected: show <piecePosition>";
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
            return "\n" + boardRenderer.renderBoard(chessGame, rotateBoard, new ArrayList<>());
        } else {
            rotateBoard = !teamColor.equals("BLACK");
            return "\n" + boardRenderer.renderBoard(chessGame, rotateBoard, new ArrayList<>());
        }

    }

    public String move(String... params) {
        if (params.length == 2) {
            try {
                ChessPosition startPosition = getCoordinates(params[0]);
                ChessPosition endPosition = getCoordinates(params[1]);
                ChessPiece.PieceType promotionPiece = null;

                if (teamColor == null) {
                    return "Observers can't move pieces";
                }
                // Check if the move involves pawn promotion
                if (isPawnPromotion(startPosition, endPosition)) {
                    promotionPiece = promptPromotionPiece(); // Ask user for promotion piece
                    if (promotionPiece == null) {
                        return "Invalid promotion piece. Move cancelled.";
                    }
                }

                // Create the move, including the promotion piece if applicable
                ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.makeMove(authToken, gameID, move);
                return "";
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException("Expected: move <startPosition> <endPosition>");
            }
        }
        throw new RuntimeException("Expected: move <startPosition> <endPosition>");
    }

    // Helper method to check if the move involves pawn promotion
    private boolean isPawnPromotion(ChessPosition startPosition, ChessPosition endPosition) {
        int endRow = endPosition.getRow();
        ChessBoard board = chessGame.getBoard();
        if (board.getPiece(startPosition) == null) {
            return false;
        }
        // Check if the piece is a pawn and reaches the final rank
        return board.getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN)
                && ((teamColor.equals("WHITE") && endRow == 8) || (teamColor.equals("BLACK") && endRow == 1));
    }



    private ChessPiece.PieceType promptPromotionPiece() {
        Scanner scanner = new Scanner(System.in); // Use Scanner to read input
        System.out.println("Your pawn can be promoted! Choose a piece: (Q)ueen, (R)ook, (B)ishop, (K)night");
        String input = scanner.nextLine().toUpperCase(); // Read user input and convert to uppercase
        // Validate user input
        switch (input) {
            case "Q":
                return ChessPiece.PieceType.QUEEN;
            case "R":
                return ChessPiece.PieceType.ROOK;
            case "B":
                return ChessPiece.PieceType.BISHOP;
            case "K":
                return ChessPiece.PieceType.KNIGHT;
            default:
                return null; // Invalid input
        }
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

    public String help() {
        return """
                - help
                - drawBoard
                - move <startPosition> <endPosition>
                - show <piecePosition> (highlights possible moves)
                - leave
                - resign
                """;
    }
}
