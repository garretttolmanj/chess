package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public MemoryUserDAO() {
//        This is for testing purposes
        users.put("garrett", new UserData("garrett", "johnson", "garrett@email.com"));
    }

    @Override
    public void clear() {
        users.clear();
    }
    @Override
    public void createUser(UserData userData){
        users.put(userData.username(), userData);
    }
    @Override
    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public String toString() {
        return "MemoryUserDAO{" +
                "users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(users);
    }
}
