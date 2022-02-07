package tu.bp21.passwortmanager.db;

import static org.junit.Assert.*;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tu.bp21.passwortmanager.StringFunction;

public class PasswordTest {

  private Password passwordObj;
  private Password wrongPasswordObj;
  private String user, website, loginName;
  private byte[] password;

  @BeforeEach
  public void setUp() throws Exception {
    user = "PasswordTestUser01";
    website = "PasswordTestWebsite01";
    loginName = "PasswordTestLoginName01";
    password = StringFunction.generateRandomString(40).getBytes();

    passwordObj = new Password(user, website, loginName, password);
    wrongPasswordObj = new Password(user, "wrong", loginName, password);
  }

  @AfterEach
  public void tearDown() throws Exception {
    user = website = loginName = "";
    password = null;
    passwordObj = wrongPasswordObj = null;
  }

  @Test
  public void testToString() {
    String expect = "[\"" + website + "\",\"" + loginName + "\"]";
    assertEquals(expect, passwordObj.toString());
    assertNotEquals(expect, wrongPasswordObj.toString());
  }
}
