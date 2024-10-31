package dataaccess;

import model.GameData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class SqlGameDAO implements GameDAO{

    public SqlGameDAO() throws DataAccessException {
        configureDatabase();
    }


    @Override
    public void clear() {

    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public void removeGame(Integer gameID) {

    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
            System.out.println("Successful");
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
