package tu.bp21.passwortmanager.js_interfaces;

import static org.junit.Assert.*;

import androidx.room.Room;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.ArrayList;

import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.ApplicationDatabase;
import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.dao.UserDao;

public class InterfacePasswordTest {
    InterfacePassword interfacePassword;
    UserDao userDao;
    PasswordDao passwordDao;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(MainActivity.class);
    public ActivityTestRule<MainActivity> getmMainActivityRule() {
        return mMainActivityRule;
    }

    @Before
    public void setUp() throws Exception {
        interfacePassword = getmMainActivityRule().getActivity().getInterfacePassword();
        userDao = Room.databaseBuilder(getmMainActivityRule().getActivity(), ApplicationDatabase.class, "database")
                .allowMainThreadQueries()
                .build().getUserDao();
        passwordDao = Room.databaseBuilder(getmMainActivityRule().getActivity(), ApplicationDatabase.class, "database")
                .allowMainThreadQueries()
                .build().getPasswordDao();
        //Clear Dummy-Data
        if(userDao.getUser("testuser05CreatePwd") != null)
            userDao.deleteUser(userDao.getUser("testuser05CreatePwd"));
        if(userDao.getUser("testuser06getPwdList") != null)
            userDao.deleteUser(userDao.getUser("testuser06getPwdList"));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreatePassword() throws Exception{
        byte[] encryptedPassword = new byte[20];
        SecureRandom.getInstanceStrong().nextBytes(encryptedPassword);
        userDao.addUser(new User("testuser05CreatePwd", "testuser05@CreatePwd.de", encryptedPassword));
        boolean worked = interfacePassword.createPassword("testuser05CreatePwd", "youtube.com", "user05LoginName", "Pwd012345");
        assertTrue(worked);

        boolean workedNot = interfacePassword.createPassword("testuser01NExist", "youtube.com", "user05LoginName", "Pwd012345");
        assertFalse(workedNot);
    }

    @Test
    public void updatePassword() {
    }

    @Test
    public void deletePassword() {
    }

    @Test
    public void testGetPasswordList() throws Exception{
        byte[] masterPassword = new byte[20];
        byte[] password1 = new byte[20];
        byte[] password2 = new byte[20];
        byte[] password3 = new byte[20];
        byte[] password4 = new byte[20];
        SecureRandom.getInstanceStrong().nextBytes(masterPassword);
        SecureRandom.getInstanceStrong().nextBytes(password1);
        SecureRandom.getInstanceStrong().nextBytes(password2);
        SecureRandom.getInstanceStrong().nextBytes(password3);
        SecureRandom.getInstanceStrong().nextBytes(password4);

        userDao.addUser(new User("testuser06getPwdList", "testuser06@getPasswordList.de", masterPassword));
        ArrayList<Password> list = new ArrayList<Password>();
        list.add(new Password("testuser06getPwdList", "App01", "appuser01", password1));
        list.add(new Password("testuser06getPwdList", "App02", "appuser02", password2));
        list.add(new Password("testuser06getPwdList", "App03", "appuser03", password3));
        list.add(new Password("testuser06getPwdList", "App04", "appuser01", password4));
        list.add(new Password("testuser06getPwdList", "App05", "appuser02", password1));
        passwordDao.addPassword(list.get(0));
        passwordDao.addPassword(list.get(1));
        passwordDao.addPassword(list.get(2));
        passwordDao.addPassword(list.get(3));
        passwordDao.addPassword(list.get(4));
        assertEquals("{\"dataArray\":" + list.toString() + "}", interfacePassword.getPasswordList("testuser06getPwdList", "067890123456"));
        assertEquals("{\"dataArray\":[]}", interfacePassword.getPasswordList("testuser06getNoPasswordList", "067890123456"));
    }

    @Test
    public void getLoginName() {
    }

    @Test
    public void getPassword() {
    }
}