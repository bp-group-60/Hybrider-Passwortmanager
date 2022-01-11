package tu.bp21.passwortmanager;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.PasswordDao;
import tu.bp21.passwortmanager.db.PasswordDatabase;
import tu.bp21.passwortmanager.db.User;

public class JavascriptHandlerTest {
    static boolean start=true;
    JavascriptHandler jsHandler;
    PasswordDao passwordDao;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(MainActivity.class);

    public ActivityTestRule<MainActivity> getmMainActivityRule() {
        return mMainActivityRule;
    }

    @Before
    public void initialise(){
        jsHandler = getmMainActivityRule().getActivity().getJavascriptHandler();
        passwordDao = Room.databaseBuilder(getmMainActivityRule().getActivity(), PasswordDatabase.class, "database")
                        .allowMainThreadQueries()
                        .build().getDao();
        //Clear Dummy-Data
        if(passwordDao.getUser("testuser02Exists") != null)
            passwordDao.deleteUser(passwordDao.getUser("testuser02Exists"));
        if(passwordDao.getUser("testuser03Check") != null)
            passwordDao.deleteUser(passwordDao.getUser("testuser03Check"));
        if(passwordDao.getUser("testuser04Create") != null)
            passwordDao.deleteUser(passwordDao.getUser("testuser04Create"));
        if(passwordDao.getUser("testuser05CreatePwd") != null)
            passwordDao.deleteUser(passwordDao.getUser("testuser05CreatePwd"));
        if(passwordDao.getUser("testuser06getPwdList") != null)
            passwordDao.deleteUser(passwordDao.getUser("testuser06getPwdList"));
    }

    @Test
    public void existUser() {
        passwordDao.addUser(new User("testuser02Exists", "testuser02@Exists.de", "023456789012"));
        assertFalse(jsHandler.existUser("testuser01NExist"));
        assertTrue(jsHandler.existUser("testuser02Exists"));
    }

    @Test
    public void checkUser() {
        passwordDao.addUser(new User("testuser03Check", "testuser03@Check.de", "034567890123"));
        jsHandler.checkUser("testuser03Check","034567890123");
        assertFalse(jsHandler.checkUser("testuser03Check","12345678012"));
        assertFalse(jsHandler.checkUser("testuser02Check","034567890123"));
        assertTrue( jsHandler.checkUser("testuser03Check","034567890123"));
    }

    @Test
    public void createUser() {
        assertTrue(passwordDao.getUser("testuser04Create") == null);
        jsHandler.createUser("testuser04Create", "testuser04@Create.de", "045678901234");
        assertTrue(passwordDao.getUser("testuser04Create") != null);
        assertEquals("testuser04Create",passwordDao.getUser("testuser04Create").getUsername());
        assertEquals("testuser04@Create.de",passwordDao.getUser("testuser04Create").getEmail());
        assertEquals("045678901234",passwordDao.getUser("testuser04Create").getPassword());
    }

    @Test
    public void createPassword() {
        passwordDao.addUser(new User("testuser05CreatePwd", "testuser05@CreatePwd.de", "056789012345"));
        boolean worked = jsHandler.createPassword("testuser05CreatePwd", "youtube.com", "user05LoginName", "Pwd012345");
        assertTrue(worked);

        boolean workedNot = jsHandler.createPassword("testuser01NExist", "youtube.com", "user05LoginName", "Pwd012345");
        assertFalse(workedNot);
    }

    @Test
    public void getPasswordList() {
        passwordDao.addUser(new User("testuser06getPwdList", "testuser06@getPasswordList.de", "067890123456"));
        ArrayList<Password> list = new ArrayList<Password>();
        list.add(new Password("testuser06getPwdList", "App01", "appuser01", "apppassword01"));
        list.add(new Password("testuser06getPwdList", "App02", "appuser02", "apppassword02"));
        list.add(new Password("testuser06getPwdList", "App03", "appuser03", "apppassword03"));
        list.add(new Password("testuser06getPwdList", "App04", "appuser01", "apppassword04"));
        list.add(new Password("testuser06getPwdList", "App05", "appuser02", "apppassword01"));
        passwordDao.addPassword(list.get(0));
        passwordDao.addPassword(list.get(1));
        passwordDao.addPassword(list.get(2));
        passwordDao.addPassword(list.get(3));
        passwordDao.addPassword(list.get(4));
        assertEquals("{\"dataArray\":" + list.toString() + "}", jsHandler.getPasswordList("testuser06getPwdList", "067890123456"));
        assertEquals("{\"dataArray\":[]}", jsHandler.getPasswordList("testuser06getNoPasswordList", "067890123456"));
    }
}