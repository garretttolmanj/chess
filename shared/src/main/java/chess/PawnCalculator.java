package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalculator extends PieceMovesCalculator{
    public PawnCalculator() {}

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        ChessGame.TeamColor pawnColor = piece.getTeamColor();
        var row = myPosition.row();
        var column = myPosition.column();

        if (pawnColor == ChessGame.TeamColor.WHITE) {
            String[] directions = {"up1", "up2", "attackUpRight", "attackUpLeft"};

            for (String direction : directions) {
                if (direction.equals("up1")) {
                    if (row + 1 > 8) {
                        continue;
                    }
                    ChessPosition up1 = new ChessPosition(row + 1, column);
                    if (board.getPiece(up1) == null) {
                        if (row + 1 == 8) {
                            for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                                if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {
                                    continue;
                                }
                                moves.add(new ChessMove(myPosition, up1, type));
                            }
                        } else {
                            moves.add(new ChessMove(myPosition, up1, null));
                        }
                    }
                } else if (direction.equals("up2")) {
                    if (row == 2 && board.getPiece(new ChessPosition(row + 1, column)) == null) {
                        ChessPosition up2 = new ChessPosition(row + 2, column );
                        if (board.getPiece(up2) == null) {
                            moves.add(new ChessMove(myPosition, up2, null));
                        }
                    }
                } else if (direction.equals("attackUpRight")) {
                    if (row + 1 > 8 || column + 1 > 8) {
                        continue;
                    }
                    ChessPosition attackUpRight = new ChessPosition(row + 1, column + 1);
                    if (board.getPiece(attackUpRight) != null) {
                        ChessPiece otherPiece = board.getPiece(attackUpRight);
                        if (otherPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                            if (row + 1 == 8) {
                                for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                                    if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {
                                        continue;
                                    }
                                    moves.add(new ChessMove(myPosition, attackUpRight, type));
                                }
                            } else {
                                moves.add(new ChessMove(myPosition, attackUpRight, null));
                            }
                        }

                    }
                } else if (direction.equals("attackUpLeft")) {
                    if (row + 1 > 8 || column - 1 < 1) {
                        continue;
                    }
                    ChessPosition attackUpLeft = new ChessPosition(row + 1, column - 1);
                    if (board.getPiece(attackUpLeft) != null) {
                        ChessPiece otherPiece = board.getPiece(attackUpLeft);
                        if (otherPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                            if (row + 1 == 8) {
                                for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                                    if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {
                                        continue;
                                    }
                                    moves.add(new ChessMove(myPosition, attackUpLeft, type));
                                }
                            } else {
                                moves.add(new ChessMove(myPosition, attackUpLeft, null));
                            }                        }
                    }
                }
            }

        } else if (pawnColor == ChessGame.TeamColor.BLACK) {
            String[] directions = {"down1", "down2", "attackDownRight", "attackDownLeft"};

            for (String direction : directions) {
                if (direction.equals("down1")) {
                    if (row - 1 < 1) {
                        continue;
                    }
                    ChessPosition down1 = new ChessPosition(row - 1, column);
                    if (board.getPiece(down1) == null) {
                        if (row - 1 == 1) {
                            for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                                if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {
                                    continue;
                                }
                                moves.add(new ChessMove(myPosition, down1, type));
                            }
                        } else {
                            moves.add(new ChessMove(myPosition, down1, null));
                        }
                    }
                } else if (direction.equals("down2")) {
                    if (row == 7 && board.getPiece(new ChessPosition(row - 1, column)) == null) {
                        ChessPosition down2 = new ChessPosition(row - 2, column );
                        if (board.getPiece(down2) == null) {
                            moves.add(new ChessMove(myPosition, down2, null));
                        }
                    }
                } else if (direction.equals("attackDownRight")) {
                    if (row - 1 < 1 || column + 1 > 8) {
                        continue;
                    }
                    ChessPosition attackDownRight = new ChessPosition(row - 1, column + 1);
                    if (board.getPiece(attackDownRight) != null) {
                        ChessPiece otherPiece = board.getPiece(attackDownRight);
                        if (otherPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            if (row - 1 == 1) {
                                for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                                    if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {
                                        continue;
                                    }
                                    moves.add(new ChessMove(myPosition, attackDownRight, type));
                                }
                            } else {
                                moves.add(new ChessMove(myPosition, attackDownRight, null));
                            }                            }
                    }
                } else if (direction.equals("attackDownLeft")) {
                    if (row - 1 < 1 || column - 1 < 1) {
                        continue;
                    }
                    ChessPosition attackDownLeft = new ChessPosition(row - 1, column - 1);
                    if (board.getPiece(attackDownLeft) != null) {
                        ChessPiece otherPiece = board.getPiece(attackDownLeft);
                        if (otherPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            if (row - 1 == 1) {
                                for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                                    if (type == ChessPiece.PieceType.PAWN || type == ChessPiece.PieceType.KING) {
                                        continue;
                                    }
                                    moves.add(new ChessMove(myPosition, attackDownLeft, type));
                                }
                            } else {
                                moves.add(new ChessMove(myPosition, attackDownLeft, null));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}
