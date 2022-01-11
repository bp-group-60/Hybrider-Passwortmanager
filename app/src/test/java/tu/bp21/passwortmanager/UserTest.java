package tu.bp21.passwortmanager;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tu.bp21.passwortmanager.db.User;

public class UserTest {
    User user;
    User wrongUser;
    User wrongUser02;
    String username;
    String email;
    String password;

    @Before
    public void setUp() throws Exception {
        username = "testusername01";
        email = "testemail01";
        password = "testpassword01";
        user = new User(username,email ,password);
        wrongUser = new User("TestUsername01", "testWrongEemail01","  testpassword01  ");
        wrongUser02 = new User("testusername01", "testemail01","  testpassword01  ");
    }

    @After
    public void tearDown() throws Exception {
         username = email = password = null;
         user = wrongUser = null;
    }

    @Test
    public void testgetUsername() {
        assertSame(username,user.getUsername());
        assertNotSame(username,wrongUser.getUsername());
        assertNotEquals(username,wrongUser.getUsername());
    }

    @Test
    public void testgetEmail() {
        assertSame(email,user.getEmail());
        assertNotSame(username,wrongUser.getEmail());
        assertNotEquals(username,wrongUser.getEmail());
    }

    @Test
    public void testgetPassword() {
        assertSame(password,user.getPassword());
        assertNotSame(username,wrongUser.getPassword());
        assertNotEquals(username,wrongUser.getPassword());
    }
}