package server.response;

import chess.ChessGame;

public record TestResponse(String authToken, String username, String password, ChessGame game) {
}