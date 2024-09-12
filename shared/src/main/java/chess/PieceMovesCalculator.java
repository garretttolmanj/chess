package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;


public class PieceMovesCalculator {

    public PieceMovesCalculator() {
    }

    protected Collection<ChessMove> iterate(ChessBoard chessBoard, ChessPosition position, ChessPiece piece, int[] direction) {
        Collection<ChessMove> movesThisDirection = new ArrayList<ChessMove>();

        int rowDirection = direction[0];
        int colDirection = direction[1];
        int currentRow = position.row();
        int currentColumn = position.column();

        while (true) {
            currentRow += rowDirection;
            currentColumn += colDirection;
            if (currentRow < 1 || currentRow > 8 || currentColumn < 1 || currentColumn > 8) {
                break;
            }

            ChessPosition newPosition = new ChessPosition(currentRow, currentColumn);

            if (chessBoard.getPiece(newPosition) == null) {
                movesThisDirection.add(new ChessMove(position, newPosition, null));
            } else {
                ChessPiece otherPiece = chessBoard.getPiece(newPosition);
                if (piece.getTeamColor().equals(otherPiece.getTeamColor())) {
                    break;
                } else {
                    movesThisDirection.add(new ChessMove(position, newPosition, null));
                    break;
                }
            }
        }
        return movesThisDirection;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        switch (piece.getPieceType()){
            case KING:
                KingCalculator Ki = new KingCalculator();
                return Ki.pieceMoves(board, myPosition, piece);
            case QUEEN:
                QueenCalculator Qu = new QueenCalculator();
                return Qu.pieceMoves(board, myPosition, piece);
            case BISHOP:
                BishopCalculator Bi = new BishopCalculator();
                return Bi.pieceMoves(board, myPosition, piece);
            case KNIGHT:
                KnightCalculator Kn = new KnightCalculator();
                return Kn.pieceMoves(board, myPosition, piece);
            case ROOK:
                RookCalculator Ro = new RookCalculator();
                return Ro.pieceMoves(board, myPosition, piece);
            case PAWN:
                PawnCalculator Pa = new PawnCalculator();
                return Pa.pieceMoves(board, myPosition, piece);
        }
        return null;
    }
}
