package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public MemoryUserDAO() {
//        This is for testing purposes
        users.put("garrett", new UserData("garrett", "johnson", "garrett@email.com"));
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
    //    public void createUser(Request req) {
//    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new DataAccessException("username not found in database");
        }
    }
}
