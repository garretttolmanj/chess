package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import requestResponse.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static ui.EscapeSequences.*;

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
                case "drawblack" -> drawBoard(false);
                case "drawwhite" -> drawBoard(true);
                case "quit" -> quit();
                default -> help();
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }


    private static final Map<ChessPiece.PieceType, String> whitePieces = Map.of(
            ChessPiece.PieceType.KING, WHITE_KING,
            ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
            ChessPiece.PieceType.ROOK, WHITE_ROOK,
            ChessPiece.PieceType.BISHOP, WHITE_BISHOP,
            ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
            ChessPiece.PieceType.PAWN, WHITE_PAWN
    );
    private static final Map<ChessPiece.PieceType, String> blackPieces = Map.of(
            ChessPiece.PieceType.KING, BLACK_KING,
            ChessPiece.PieceType.QUEEN, BLACK_QUEEN,
            ChessPiece.PieceType.ROOK, BLACK_ROOK,
            ChessPiece.PieceType.BISHOP, BLACK_BISHOP,
            ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
            ChessPiece.PieceType.PAWN, BLACK_PAWN
    );
    private String[] alphabet = new String[] {"a", " b ", "c", "d", " e", " f", "g", "h"};

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return "";
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? whitePieces.get(piece.getPieceType())
                : blackPieces.get(piece.getPieceType());
    }

    public String drawBoard(boolean rotated) {
        ChessGame chessGame = new ChessGame();
        ChessBoard chessBoard = chessGame.getBoard();
        StringBuilder boardDrawing = new StringBuilder();

        // Loop through rows and columns to create a border and draw pieces
        int startRow = rotated ? 9 : 0;
        int endRow = rotated ? -1: 10;
        int startCol = rotated ? 9 : 0;
        int endCol = rotated ? -1: 10;
        int i = rotated ? -1 : 1;

        for (int row = startRow; row != endRow; row+=i) {
            for (int col = startCol; col != endCol; col+=i) {
                String square;

                // Check if the current position is part of the border
                if (row == 0 || row == 9 || col == 0 || col == 9) {
                    if (col == 0 || col == 9) {
                        // Left and right border labels (row numbers)
                        square = SET_BG_COLOR_BLACK + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE + " " +
                                ((row > 0 && row < 9) ? String.valueOf(9 - row) : " ") + " ";

                    } else if (row == 0 || row == 9) {
                        // Top and bottom border labels (column letters)
                        square = SET_BG_COLOR_BLACK + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE + " " + alphabet[col - 1] + " ";
                    } else {
                        square = SET_BG_COLOR_BLACK + EMPTY;
                    }
                } else {
                    // Calculate the chessboard square colors
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece chessPiece = chessBoard.getPiece(position);
                    String pieceSymbol = getPieceSymbol(chessPiece);

                    if ((row % 2 == 0 && col % 2 == 0) || (row % 2 != 0 && col % 2 != 0)) {
                        square = SET_TEXT_FAINT + SET_BG_COLOR_TAN + (pieceSymbol.isEmpty() ? EMPTY : SET_TEXT_COLOR_BLACK + pieceSymbol);
                    } else {
                        square = SET_TEXT_FAINT+ SET_BG_COLOR_BROWN + (pieceSymbol.isEmpty() ? EMPTY : SET_TEXT_COLOR_BLACK + pieceSymbol);
                    }
                }

                boardDrawing.append(square);
            }
            boardDrawing.append("\n");  // Reset background and add a newline after each row
        }

        return boardDrawing + RESET_BG_COLOR;
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
