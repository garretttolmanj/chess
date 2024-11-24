package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.util.Map;

import static ui.EscapeSequences.*;

public class BoardRenderer {
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

    private final String[] alphabet = new String[]{
            A_FULL_WIDTH,
            B_FULL_WIDTH,
            C_FULL_WIDTH,
            D_FULL_WIDTH,
            E_FULL_WIDTH,
            F_FULL_WIDTH,
            G_FULL_WIDTH,
            H_FULL_WIDTH};

    public String renderBoard(ChessGame chessGame, boolean rotateBoard) {
        ChessBoard chessBoard = chessGame.getBoard();
        StringBuilder boardDrawing = new StringBuilder();

        int startRow = rotateBoard ? 9 : 0;
        int endRow = rotateBoard ? -1 : 10;
        int rowIncrement = rotateBoard ? -1 : 1;

        int startCol = rotateBoard ? 0 : 9;
        int endCol = rotateBoard ? 10 : -1;
        int colIncrement = rotateBoard ? 1 : -1;

        for (int row = startRow; row != endRow; row += rowIncrement) {
            for (int col = startCol; col != endCol; col += colIncrement) {
                String square;

                // Border logic
                if (row == 0 || row == 9 || col == 0 || col == 9) {
                    if (col == 0 || col == 9) {
                        square = SET_BG_COLOR_BLACK + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE + " " +
                                ((row > 0 && row < 9) ? String.valueOf(row) : " ") + " ";
                    } else if (row == 0 || row == 9) {
                        square = SET_BG_COLOR_BLACK + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE + " " + alphabet[col - 1] + " ";
                    } else {
                        square = SET_BG_COLOR_BLACK + EMPTY;
                    }
                } else {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece chessPiece = chessBoard.getPiece(position);
                    String pieceSymbol = getPieceSymbol(chessPiece);

                    if ((row % 2 == 0 && col % 2 == 0) || (row % 2 != 0 && col % 2 != 0)) {
                        square = SET_TEXT_FAINT + SET_BG_COLOR_BROWN + (pieceSymbol.isEmpty() ? EMPTY : SET_TEXT_COLOR_BLACK + pieceSymbol);
                    } else {
                        square = SET_TEXT_FAINT + SET_BG_COLOR_TAN + (pieceSymbol.isEmpty() ? EMPTY : SET_TEXT_COLOR_BLACK + pieceSymbol);
                    }
                }
                boardDrawing.append(square);
            }
            boardDrawing.append("\n");
        }

        return boardDrawing + RESET_BG_COLOR;
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return "";
        }
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? WHITE_PIECES.get(piece.getPieceType())
                : BLACK_PIECES.get(piece.getPieceType());
    }
}
