package chess;

import java.util.Collection;

public class KingCalc extends PieceMovesCalculator {

    public KingCalc() {
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        int[][] kingDirections = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        return getMovesInDirections(board, position, piece, kingDirections);
    }
}


