package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    final private ArrayList<GameData> games = new ArrayList<>();

    public MemoryGameDAO() {}

    @Override
    public ArrayList<GameData> listGames() {
        return games;
    }

    @Override
    public void createGame(GameData game) {
        games.add(game);
    }
}
