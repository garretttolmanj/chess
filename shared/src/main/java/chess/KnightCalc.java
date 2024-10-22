package chess;

import java.util.Collection;

public class KnightCalc extends PieceMovesCalculator {

    public KnightCalc() {
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        int[][] knightDirections = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
        return getMovesInDirections(board, position, piece, knightDirections);
    }
}
