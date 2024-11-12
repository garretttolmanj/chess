package dataaccess;

import model.*;

public interface AuthDAO {
    void clear() throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    int length() throws DataAccessException;
}
