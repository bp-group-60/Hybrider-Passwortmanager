package tu.bp21.passwortmanager.db;

import static org.junit.Assert.*;

import org.junit.jupiter.api.*;

import tu.bp21.passwortmanager.StringFunction;
import tu.bp21.passwortmanager.db.entities.Website;

class WebsiteTest {

  private Website websiteEntity;
  private Website wrongWebsiteEntity;
  private String username, websiteName, loginName;
  private byte[] password;

  @BeforeEach
  void setUp() throws Exception {
    username = "PasswordTestUser01";
    websiteName = "PasswordTestWebsite01";
    loginName = "PasswordTestLoginName01";
    password = StringFunction.generateRandomString(40).getBytes();

    websiteEntity = new Website(username, websiteName, loginName, password);
    wrongWebsiteEntity = new Website(username, "wrong", loginName, password);
  }

  @Test
  void testToString() {
    String expect = "[\"" + websiteName + "\",\"" + loginName + "\"]";
    assertEquals(expect, websiteEntity.toString());
    assertNotEquals(expect, wrongWebsiteEntity.toString());
  }
}
