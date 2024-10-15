package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, String> sessions = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        sessions.put(authData.authToken(), authData.username());
    }

}
