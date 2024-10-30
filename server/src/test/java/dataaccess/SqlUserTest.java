package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SqlUserTest {

    @BeforeAll
    public static void setUp() {
        SqlUserDAO sqlUser = new SqlUserDAO();
    }

    @Test
    public void readFile() {
        System.out.println("Hello World");
    }
}
