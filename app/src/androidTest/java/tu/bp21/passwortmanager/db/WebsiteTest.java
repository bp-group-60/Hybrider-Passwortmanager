package tu.bp21.passwortmanager.db;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import static tu.bp21.passwortmanager.StringFunction.*;

import tu.bp21.passwortmanager.db.entities.Website;

class WebsiteTest {

  @Test
  void testToString() {
    String user = generateRandomString(40);
    String website = generateRandomString(40);
    String url = generateRandomString(20) + ".com";
    Website websiteObject = new Website(user, website, url);

    String expected = "\"" + url + "\"";
    String actual = websiteObject.toString();
    assertEquals(expected, actual);
  }
}
