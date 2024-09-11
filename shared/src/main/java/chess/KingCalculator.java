package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalculator extends PieceMovesCalculator{
//    How do I reference the board that to calculate the moves?
    public KingCalculator() {}

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        var row = myPosition.getRow();
        var column = myPosition.getColumn();

        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        ChessPosition up1 = new ChessPosition(row, column + 1);
        if (board.getPiece(up1) == null) {
            moves.add(new ChessMove(myPosition, up1, null));
        }

        return moves;
    }
}