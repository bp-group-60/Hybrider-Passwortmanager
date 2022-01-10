package tu.bp21.passwortmanager;

import static org.junit.Assert.*;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class JavascriptHandlerTest {
    JavascriptHandler jsHandler;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(MainActivity.class);

    public ActivityTestRule<MainActivity> getmMainActivityRule() {
        return mMainActivityRule;
    }

    @Before
    public void initialise(){
        jsHandler = getmMainActivityRule().getActivity().getJavascriptHandler();
    }

    /*@After
    public void tearDown() throws Exception {
    }*/

    @Test
    public void existUser() {
    }

    @Test
    public void checkUser() {
    }

    @Test
    public void createUser() {
        jsHandler.createUser("testuser01", "test@test.de", "123456789123456789");
        assertEquals(true, jsHandler.existUser("testuser01"));

        assertEquals(false, jsHandler.existUser("testuser00"));
    }

    @Test
    public void createPassword() {
    }

    @Test
    public void getPasswordList() {
    }
}