package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import requestResponse.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;

public class GameClient implements Client{
    private final ServerFacade server;
    private final Repl repl;
    private final String authToken;
    private final Integer gameID;
    private String teamColor;

    public GameClient(String serverUrl, Repl repl, String authToken, Integer gameID, String teamColor) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "draw" -> drawBoard();
                case "exit" -> exit();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String drawBoard() {
        ChessGame chessGame = new ChessGame();
        ChessBoard chessBoard = chessGame.getBoard();
        String boardDrawing = "";
        int row = 1;
        while (row < 9) {
            int col = 1;
            while (col < 9 ) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(position);
                boardDrawing += "[ ]";
                col++;
            }
            boardDrawing += "\n";
            row ++;
        }
        return boardDrawing;
    }

    public String exit() {
        repl.signIn(authToken);
        return "Left Game";
    }

    public String help() {
        return """
                - CHESS!!!!
                - exit
                """;
    }
}
