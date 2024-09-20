package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public PieceMovesCalculator() {
    }

    protected Collection<ChessMove> iterate(ChessBoard board, ChessPosition position, ChessPiece piece, int[] direction) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = position.getRow();
        int column = position.getColumn();
        int currentRow = row;
        int currentColumn = column;

        while(true) {
            currentRow += direction[0];
            currentColumn += direction[1];
            if (currentRow < 1 || currentRow > 8 || currentColumn < 1 || currentColumn > 8) {
                break;
            }
            ChessPosition newPosition = new ChessPosition(currentRow, currentColumn);
            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
            } else {
                ChessPiece newPiece = board.getPiece(newPosition);
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

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                KingCalc King = new KingCalc();
                return King.pieceMoves(board, position, piece);
            case QUEEN:
                QueenCalc Queen = new QueenCalc();
                return Queen.pieceMoves(board, position, piece);
            case BISHOP:
                BishopCalc Bishop = new BishopCalc();
                return Bishop.pieceMoves(board, position, piece);
            case KNIGHT:
                KnightCalc Knight = new KnightCalc();
                return Knight.pieceMoves(board, position, piece);
            case ROOK:
                RookCalc Rook = new RookCalc();
                return Rook.pieceMoves(board, position, piece);
            case PAWN:
                PawnCalc Pawn = new PawnCalc();
                return Pawn.pieceMoves(board, position, piece);
        }
        return null;
    }
}
