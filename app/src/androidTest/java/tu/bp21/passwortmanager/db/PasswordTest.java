package tu.bp21.passwortmanager.db;

import static org.junit.Assert.*;

import org.junit.jupiter.api.*;

import tu.bp21.passwortmanager.StringFunction;
import tu.bp21.passwortmanager.db.entities.Password;

public class PasswordTest {

  private Password passwordEntity;
  private Password wrongPasswordEntity;
  private String username, websiteName, loginName;
  private byte[] password;

  @BeforeEach
  public void setUp() throws Exception {
    username = "PasswordTestUser01";
    websiteName = "PasswordTestWebsite01";
    loginName = "PasswordTestLoginName01";
    password = StringFunction.generateRandomString(40).getBytes();

    passwordEntity = new Password(username, websiteName, loginName, password);
    wrongPasswordEntity = new Password(username, "wrong", loginName, password);
  }

  @AfterEach
  public void tearDown() throws Exception {
    username = websiteName = loginName = "";
    password = null;
    passwordEntity = wrongPasswordEntity = null;
  }

  @Test
  public void testToString() {
    String expect = "[\"" + websiteName + "\",\"" + loginName + "\"]";
    assertEquals(expect, passwordEntity.toString());
    assertNotEquals(expect, wrongPasswordEntity.toString());
  }
}
