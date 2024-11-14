package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.*;
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
                case "quit" -> quit();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    private String[] alphabet = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
    public String drawBoard() {
        ChessGame chessGame = new ChessGame();
        ChessBoard chessBoard = chessGame.getBoard();
        String boardDrawing = "";

        // Loop through rows and columns to create a border
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                String square;

                // Check if the current position is part of the border
                if (row == 0 || row == 9 || col == 0 || col == 9) {
                    if (col == 0 || col == 9) {
                        // Left and right border labels (row numbers)
                        square = SET_BG_COLOR_LIGHT_BLUE + SET_TEXT_COLOR_BLACK + "  " +
                                ((row > 0 && row < 9) ? String.valueOf(9 - row) : " ") + " ";
                    } else if (row == 0 || row == 9) {
                        // Top and bottom border labels (column letters)
                        square = SET_BG_COLOR_LIGHT_BLUE + SET_TEXT_COLOR_BLACK + " " + alphabet[col - 1]+ "  ";
                    } else {
                        square = SET_BG_COLOR_LIGHT_BLUE + EMPTY;
                    }
                } else {
                    // Calculate the chessboard square colors
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = chessBoard.getPiece(position);

                    if ((row % 2 == 0 && col % 2 == 0) || (row % 2 != 0 && col % 2 != 0)) {
                        // Light square
                        square = SET_BG_COLOR_TAN + (piece == null ? EMPTY : SET_TEXT_COLOR_BLACK + BLACK_PAWN);
                    } else {
                        // Dark square
                        square = SET_BG_COLOR_BROWN + (piece == null ? EMPTY : SET_TEXT_COLOR_BLACK + BLACK_PAWN);
                    }
                }

                boardDrawing += square;
            }
            boardDrawing += RESET_BG_COLOR + "\n";  // Reset background and add a newline after each row
        }

        return boardDrawing;
    }



    public String quit() {
        repl.signIn(authToken);
        return "Left Game";
    }

    public String help() {
        return """
                - CHESS!!!!
                - quit
                """;
    }
}
