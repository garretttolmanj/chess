import chess.*;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import server.Server;
import service.UserService;
import spark.*;
public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server chessServer = new Server();
        chessServer.run(8080);
    }
}