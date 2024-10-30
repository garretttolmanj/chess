package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SqlUserTest {

    private static SqlUserDAO sqlUser;
//
//    @BeforeAll
//    public static void setUp() throws DataAccessException {
//        SqlUserDAO sqlUser = new SqlUserDAO();
//    }

    @Test
    public void createUserPositive() throws DataAccessException {
        SqlUserDAO sqlUser = new SqlUserDAO();
        UserData testUser = new UserData("user", "password", "email@mail.com");
        sqlUser.createUser(testUser);
    }
}
