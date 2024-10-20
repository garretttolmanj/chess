package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    final private ArrayList<GameData> games = new ArrayList<>();

    public MemoryGameDAO() {}

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public ArrayList<GameData> listGames() {
        return games;
    }

    @Override
    public void createGame(GameData game) {
        games.add(game);
    }
    @Override
    public void removeGame(Integer gameID) {
        games.removeIf(game -> game.gameID() == gameID);
    }

    @Override
    public GameData getGame(Integer gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryGameDAO that = (MemoryGameDAO) o;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(games);
    }
    public int length() {
        return games.size();
    }
}
