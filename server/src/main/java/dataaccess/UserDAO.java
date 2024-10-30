package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear();

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username);

    int length();

//    void removeUser(String username);
}
