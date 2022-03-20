package tu.bp21.passwortmanager.db;

import static org.junit.Assert.*;

import org.junit.jupiter.api.*;

import tu.bp21.passwortmanager.StringFunction;
import tu.bp21.passwortmanager.db.entities.Website;

public class WebsiteTest {

  private Website websiteEntity;
  private Website wrongWebsiteEntity;
  private String username, websiteName, loginName;
  private byte[] password;

  @BeforeEach
  public void setUp() throws Exception {
    username = "PasswordTestUser01";
    websiteName = "PasswordTestWebsite01";
    loginName = "PasswordTestLoginName01";
    password = StringFunction.generateRandomString(40).getBytes();

    websiteEntity = new Website(username, websiteName, loginName, password);
    wrongWebsiteEntity = new Website(username, "wrong", loginName, password);
  }

  @AfterEach
  public void tearDown() throws Exception {
    username = websiteName = loginName = "";
    password = null;
    websiteEntity = wrongWebsiteEntity = null;
  }

  @Test
  public void testToString() {
    String expect = "[\"" + websiteName + "\",\"" + loginName + "\"]";
    assertEquals(expect, websiteEntity.toString());
    assertNotEquals(expect, wrongWebsiteEntity.toString());
  }
}
