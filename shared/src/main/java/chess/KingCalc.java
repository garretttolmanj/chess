package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalc extends PieceMovesCalculator {
    public KingCalc() {
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {1, 1}, {0, -1}, {1, -1}};

        int row = position.getRow();
        int column = position.getColumn();

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newColumn = column + direction[1];

            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                } else {
                    ChessPiece newPiece = board.getPiece(newPosition);
                    if (piece.getTeamColor() != newPiece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }
        }

        return moves;
    }
}
