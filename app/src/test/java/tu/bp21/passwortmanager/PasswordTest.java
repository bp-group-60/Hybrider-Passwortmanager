package tu.bp21.passwortmanager;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tu.bp21.passwortmanager.db.Password;

public class PasswordTest {

    private Password passwordObj;
    private Password wrongPasswordObj;
    private String user, website, loginName,password;

    @Before
    public void setUp() throws Exception {
        user = "PasswordTestUser01";
        website = "PasswordTestWebsite01";
        loginName = "PasswordTestLoginName01";
        password =  "passwordTestpwd01";

        passwordObj = new Password(user,website,loginName,password);
        wrongPasswordObj = new Password(user,"wrong",loginName,"password");
    }

    @After
    public void tearDown() throws Exception {
        user = website = loginName = password = "";
        passwordObj = wrongPasswordObj = null;
    }

    @Test
    public void toSecureString() {
        String expect = "[\"" + website + "\",\"" + loginName + "\"]";
        assertEquals(expect, passwordObj.toSecureString());
        assertNotEquals(expect,wrongPasswordObj.toSecureString());

    }

    @Test
    public void testToString() {
        String expect = "[\"" + website + "\",\"" + loginName + "\",\"" + password + "\"]";
        assertEquals(expect, passwordObj.toString());
        assertNotEquals(expect,wrongPasswordObj.toString());
    }
}