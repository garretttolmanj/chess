package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;
//    void createUser(Request req) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
