package tu.bp21.passwortmanager.db;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import static tu.bp21.passwortmanager.StringFunction.*;

import tu.bp21.passwortmanager.db.entities.Website;

class WebsiteTest {

  @Test
  void testToString() {
    String username = generateRandomString(40);
    String websiteName = generateRandomString(40);
    String url = generateRandomString(20) + ".com";
    Website websiteEntity = new Website(username, websiteName, url);

    String expected = "\"" + url + "\"";
    String actual = websiteEntity.toString();
    assertEquals(expected, actual);
  }
}
