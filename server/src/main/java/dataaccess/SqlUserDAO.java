package dataaccess;

import model.UserData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SqlUserDAO implements UserDAO {
    private static final Properties properties = new Properties();
    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";

    // Static block to load properties once when the class is loaded
    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to load db.properties file", ex);
        }
    }

    // Retrieve properties as needed
    public static final String user = properties.getProperty("db.user");
    public static final String password = properties.getProperty("db.password");
    public static final String url = properties.getProperty("db.url");

    public SqlUserDAO() {
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    // Class constructor and other methods here
}

