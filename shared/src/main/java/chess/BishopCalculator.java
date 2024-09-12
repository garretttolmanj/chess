package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopCalculator extends PieceMovesCalculator{
    public BishopCalculator() {}


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = { {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };

        for (int[] direction : directions) {
            Collection<ChessMove> newMoves = iterate(board, myPosition, piece, direction);
            moves.addAll(newMoves);
        }
        return moves;
    }

}
