package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor playerTurn;
    private ChessBoard gameBoard = new ChessBoard();
//    Attributes for Castling
    private boolean whiteKingHasntMoved;
    private boolean whiteRightRookHasntMoved;
    private boolean whiteLeftRookHasntMoved;
    private boolean blackKingHasntMoved;
    private boolean blackRightRookHasntMoved;
    private boolean blackLeftRookHasntMoved;


    public ChessGame() {
        this.playerTurn = TeamColor.WHITE;
        this.gameBoard.resetBoard();

        this.whiteKingHasntMoved = true;
        this.whiteRightRookHasntMoved = true;
        this.whiteLeftRookHasntMoved = true;
        this.blackKingHasntMoved = true;
        this.blackRightRookHasntMoved = true;
        this.blackLeftRookHasntMoved = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return playerTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        playerTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece piece = gameBoard.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessPiece enemyPiece = gameBoard.getPiece(move.getEndPosition());
            testMove(move);
            boolean safe = !isInCheck(piece.getTeamColor());
            reverseTestMove(move, enemyPiece);
            if (safe && !validMoves.contains(move)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        ChessPiece piece = gameBoard.getPiece(startPosition);
        if (piece == null) {
            throw new InvalidMoveException("No piece at that starting position");
        }

        if (piece.getTeamColor() != playerTurn) {
            throw new InvalidMoveException("Forget about it!!");
        }
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Get that corn outta my face!!");
        }
        if (promotionPiece != null) {
            ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), promotionPiece);
            gameBoard.addPiece(endPosition, newPiece);
            gameBoard.removePiece(startPosition);
            playerTurn = (playerTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            gameBoard.addPiece(endPosition, piece);
            gameBoard.removePiece(startPosition);
            playerTurn = (playerTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            if (piece.getPieceType() == ChessPiece.PieceType.KING || piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                kingOrRookMoved(move, piece);
            }
        }
    }

    public void kingOrRookMoved(ChessMove move, ChessPiece piece) {
        int row = move.getStartPosition().getRow();
        int col = move.getStartPosition().getColumn();

        TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();
        if (color == TeamColor.WHITE && type == ChessPiece.PieceType.KING) {
            this.whiteKingHasntMoved = false;
        }
        if (color == TeamColor.BLACK && type == ChessPiece.PieceType.KING) {
            this.blackKingHasntMoved = false;
        }
        if (color == TeamColor.WHITE && type == ChessPiece.PieceType.ROOK) {
            if (row == 1 && col == 1) {
                this.whiteLeftRookHasntMoved = false;
            }
            if (row == 1 && col == 8) {
                this.whiteRightRookHasntMoved = false;
            }
        }
        if (color == TeamColor.BLACK && type == ChessPiece.PieceType.ROOK) {
            if (row == 8 && col == 1) {
                this.blackLeftRookHasntMoved = false;
            }
            if (row == 8 && col == 8) {
                this.blackRightRookHasntMoved = false;
            }
        }
    }


    public void testMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece piece = gameBoard.getPiece(startPosition);
        gameBoard.addPiece(endPosition, piece);
        gameBoard.removePiece(startPosition);
    }
    public void reverseTestMove(ChessMove move, ChessPiece enemyPiece) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece piece = gameBoard.getPiece(endPosition);
        gameBoard.addPiece(startPosition, piece);
        gameBoard.addPiece(endPosition,enemyPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }
        TeamColor enemyTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> enemyMoves = generateTeamMoves(enemyTeam);
        for (ChessMove move : enemyMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    private Collection<ChessMove> generateTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> teamMoves = new ArrayList<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    teamMoves.addAll(piece.pieceMoves(gameBoard, position));
                }
            }
        }
        return teamMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> allMoves = generateTeamMoves(teamColor);
        Collection<ChessMove> allValidMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            ChessPosition position = move.getStartPosition();
            allValidMoves.addAll(validMoves(position));
        }
        return allValidMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> allMoves = generateTeamMoves(teamColor);
        Collection<ChessMove> allValidMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            ChessPosition position = move.getStartPosition();
            allValidMoves.addAll(validMoves(position));
        }
        return allValidMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
