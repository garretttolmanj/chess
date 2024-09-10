package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;


public class PieceMovesCalculator {

    public PieceMovesCalculator() {
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        switch (piece.getPieceType()){
            case KING:
                KingCalculator Ki = new KingCalculator();
                break;
            case QUEEN:
                QueenCalculator Qu = new QueenCalculator();
                break;
            case BISHOP:
                BishopCalculator Bi = new BishopCalculator();
                break;
            case KNIGHT:
                KnightCalculator Kn = new KnightCalculator();
                break;
            case ROOK:
                RookCalculator Ro = new RookCalculator();
                break;
            case PAWN:
                PawnCalculator Pa = new PawnCalculator();
                break;
        }
        return null;
    }
}
