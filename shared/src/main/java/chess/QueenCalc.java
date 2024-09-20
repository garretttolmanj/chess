package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenCalc extends PieceMovesCalculator{
    public QueenCalc(){}

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {1, 1}, {0, -1}, {1, -1}};
        for (int[] direction : directions) {
            Collection<ChessMove> movesThisDirection = iterate(board, position, piece, direction);
            moves.addAll(movesThisDirection);
        }

        return moves;
    }
}