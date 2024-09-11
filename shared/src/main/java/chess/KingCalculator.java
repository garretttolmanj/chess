package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalculator extends PieceMovesCalculator{
//    How do I reference the board that to calculate the moves?
    public KingCalculator() {}

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = {
                {1, 0}, {1, 1}, {0, 1}, {-1, 1},  // Up, Up-Right, Right, Down-Right
                {-1, 0}, {-1, -1}, {0, -1}, {1, -1}  // Down, Down-Left, Left, Up-Left
        };
        var row = myPosition.row();
        var column = myPosition.column();

        for (int []direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];
            ChessPosition newPosition = new ChessPosition(newRow, newColumn);

            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    ChessPiece otherPiece = board.getPiece(newPosition);
                    if (piece.getTeamColor().equals(otherPiece.getTeamColor())) {
                        continue;
                    }
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

        }
        return moves;
    }
}