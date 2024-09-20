package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalc extends PieceMovesCalculator{
    public PawnCalc(){}

    private void oneMove(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int direction, int promotionRow){
        int newRow = position.getRow() + direction;
        int column = position.getColumn();

        ChessPosition newPosition = new ChessPosition(newRow, column);
        if (board.getPiece(newPosition) == null){
            if (newRow == promotionRow) {
                promotionHelper(moves, position, newPosition);
            } else {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
    }

    private void twoMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int direction, int startRow){
        int row = position.getRow();
        int column = position.getColumn();

        if (row == startRow) {
            ChessPosition move1 = new ChessPosition(row + direction, column);
            ChessPosition move2 = new ChessPosition(row + (2 * direction), column);

            if (board.getPiece(move1) == null && board.getPiece(move2) == null){
                moves.add(new ChessMove(position, move2, null));
            }
        }

    }
    private void attackMove(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, ChessPiece piece, int newColumn, int direction, int promotionRow) {
        int newRow = position.getRow() + direction;

        ChessPosition attackPosition = new ChessPosition(newRow, newColumn);
        if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
            if (board.getPiece(attackPosition) != null){
                ChessPiece otherPiece = board.getPiece(attackPosition);
                if (piece.getTeamColor() != otherPiece.getTeamColor()) {
                    if (newRow == promotionRow) {
                        promotionHelper(moves, position, attackPosition);
                    } else{
                        moves.add(new ChessMove(position, attackPosition, null));
                    }
                }
            }
        }
    }
    private void promotionHelper(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        for (ChessPiece.PieceType pieceType : ChessPiece.PieceType.values()) {
            if (pieceType == ChessPiece.PieceType.PAWN || pieceType == ChessPiece.PieceType.KING) {
                continue;
            }
            moves.add(new ChessMove(startPosition, endPosition, pieceType));
        }
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;

        int column = position.getColumn();

        oneMove(board, position, moves, direction, promotionRow);
        twoMoves(board, position, moves, direction, startRow);
        attackMove(board, position, moves, piece,column + 1, direction, promotionRow);
        attackMove(board, position, moves, piece,column - 1, direction, promotionRow);

        return moves;
    }
}
