package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> sessions = new HashMap<>();

    public MemoryAuthDAO() {}

    @Override
    public void clear() {
        sessions.clear();
    }

    @Override
    public void createAuth(AuthData authData){
        sessions.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return sessions.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        sessions.remove(authToken);
    }

    @Override
    public String toString() {
        return "MemoryAuthDAO{" +
                "sessions=" + sessions +
                '}';
    }
    @Override
    public int length() {
        return sessions.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(sessions, that.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessions);
    }
}
