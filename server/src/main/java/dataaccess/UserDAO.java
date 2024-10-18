package dataaccess;

import model.UserData;
import server.request.RegisterRequest;

public interface UserDAO {
    void clear();
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
