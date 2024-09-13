package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.row() - 1][position.column() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.row() - 1][position.column() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        ChessPiece.PieceType[] backRow = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
        };

        for (int column = 1; column <= 8; column++) {
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

            ChessPiece whitePiece = new ChessPiece(ChessGame.TeamColor.WHITE, backRow[column - 1]);
            ChessPiece blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, backRow[column - 1]);

            ChessPosition whitePawnPosition = new ChessPosition(2, column);
            ChessPosition blackPawnPosition = new ChessPosition(7, column);

            ChessPosition whitePiecePosition = new ChessPosition(1, column);
            ChessPosition blackPiecePosition = new ChessPosition(8, column);

            addPiece(whitePawnPosition, whitePawn);
            addPiece(blackPawnPosition, blackPawn);
            addPiece(whitePiecePosition, whitePiece);
            addPiece(blackPiecePosition, blackPiece);
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard otherBoard = (ChessBoard) obj;

        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece thisPiece = this.getPiece(position);
                ChessPiece otherPiece = otherBoard.getPiece(position);

                if (thisPiece == null && otherPiece == null) {
                    continue;
                }
                if (thisPiece == null || otherPiece == null || !thisPiece.equals(otherPiece)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int row = 1; row <= 8; row++) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece thisPiece = this.getPiece(position);
                if (thisPiece == null) {
                    continue;
                }
                result = 31 * result + thisPiece.hashCode();
            }
        }
        return result;
    }
}
