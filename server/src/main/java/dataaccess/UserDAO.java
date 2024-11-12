package dataaccess;

import model.*;

public interface UserDAO {
    void clear() throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    int length() throws DataAccessException;

//    void removeUser(String username) throws DataAccessException;
}
