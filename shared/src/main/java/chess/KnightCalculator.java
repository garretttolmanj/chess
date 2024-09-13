package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalculator extends PieceMovesCalculator{
    public KnightCalculator() {}

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = { {2,1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1, -2}, {2, -1} };

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