package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public PieceMovesCalculator() {
    }

    // Helper method for non-repeating moves (like King, Knight, etc.)
    protected Collection<ChessMove> getMovesInDirections(ChessBoard board,
                                                         ChessPosition position,
                                                         ChessPiece piece,
                                                         int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = position.getRow();
        int column = position.getColumn();

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null || piece.getTeamColor() != newPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        }

        return moves;
    }

    // Helper method for pieces that move across the board.
    protected Collection<ChessMove> iterate(ChessBoard board, ChessPosition position, ChessPiece piece, int[] direction) {
        Collection<ChessMove> moves = new ArrayList<>();

        int currentRow = position.getRow();
        int currentColumn = position.getColumn();

        while (true) {
            currentRow += direction[0];
            currentColumn += direction[1];
            if (currentRow < 1 || currentRow > 8 || currentColumn < 1 || currentColumn > 8) {
                break;
            }
            ChessPosition newPosition = new ChessPosition(currentRow, currentColumn);
            ChessPiece newPiece = board.getPiece(newPosition);

            if (newPiece == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else {
                if (piece.getTeamColor() != newPiece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }

    // Main pieceMoves method that calls specific piece calculators.
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return new KingCalc().pieceMoves(board, position, piece);
            case QUEEN:
                return new QueenCalc().pieceMoves(board, position, piece);
            case BISHOP:
                return new BishopCalc().pieceMoves(board, position, piece);
            case KNIGHT:
                return new KnightCalc().pieceMoves(board, position, piece);
            case ROOK:
                return new RookCalc().pieceMoves(board, position, piece);
            case PAWN:
                return new PawnCalc().pieceMoves(board, position, piece);
        }
        return null;
    }
}