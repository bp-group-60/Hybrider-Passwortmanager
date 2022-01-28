package tu.bp21.passwortmanager.js_interfaces;

import static org.junit.Assert.*;

import androidx.room.Room;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.ApplicationDatabase;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.dao.UserDao;

public class InterfaceUserTests {
    InterfaceUser interfaceUser;
    UserDao userDao;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<>(MainActivity.class);
    public ActivityTestRule<MainActivity> getMainActivityRule() {
        return mainActivityRule;
    }

    @Before
    public void setUp() throws Exception {
        interfaceUser = getMainActivityRule().getActivity().getInterfaceUser();
        userDao = Room.databaseBuilder(getMainActivityRule().getActivity(), ApplicationDatabase.class, "database")
                .allowMainThreadQueries()
                .build().getUserDao();
    }

    @After
    public void tearDown() throws Exception {
        //Clear Dummy-Data
        if(userDao.getUser("testuser02Exists") != null)
            userDao.deleteUser(userDao.getUser("testuser02Exists"));
        if(userDao.getUser("testuser03Check") != null)
            userDao.deleteUser(userDao.getUser("testuser03Check"));
        if(userDao.getUser("testuser04Create") != null)
            userDao.deleteUser(userDao.getUser("testuser04Create"));
    }

    @Test
    public void testExistUser() throws Exception {
        byte[] encryptedPassword = new byte[20];
        SecureRandom.getInstanceStrong().nextBytes(encryptedPassword);
        userDao.addUser(new User("testuser02Exists", "testuser02@Exists.de", encryptedPassword));
        assertFalse(interfaceUser.existUser("testuser01NExist"));
        assertTrue(interfaceUser.existUser("testuser02Exists"));
    }

    @Test
    public void testCheckUser() {
        String password = "034567890123";
        Crypto.setSalt(Crypto.generateSalt(16));
        byte[] encryptedPassword = Crypto.computeHash(password);
        userDao.addUser(new User("testuser03Check", "testuser03@Check.de", encryptedPassword));
        assertFalse(interfaceUser.checkUser("testuser03Check","12345678012"));
        assertFalse(interfaceUser.checkUser("testuser02Check","034567890123"));
        assertTrue( interfaceUser.checkUser("testuser03Check","034567890123"));
    }

    @Test
    public void testCreateUser() {
        String password = "045678901234";
        byte[] encryptedPassword;
        assertTrue(userDao.getUser("testuser04Create") == null);
        interfaceUser.createUser("testuser04Create", "testuser04@Create.de", password);
        Crypto.setSalt(Arrays.copyOf(userDao.getUser("testuser04Create").password, 16));
        encryptedPassword = Crypto.computeHash(password);
        assertTrue(userDao.getUser("testuser04Create") != null);
        assertEquals("testuser04Create", userDao.getUser("testuser04Create").username);
        assertEquals("testuser04@Create.de", userDao.getUser("testuser04Create").email);
        assertTrue(Arrays.equals(encryptedPassword, userDao.getUser("testuser04Create").password));
    }

    @Test
    public void deleteUser() {
    }
}