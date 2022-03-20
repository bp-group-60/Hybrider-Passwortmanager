package tu.bp21.passwortmanager.js_interfaces.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.convertNullToEmptyString;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;
import java.util.Random;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import tu.bp21.passwortmanager.db.data_access_objects.UserDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.UrlDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Website;
import tu.bp21.passwortmanager.db.entities.Url;
import tu.bp21.passwortmanager.db.entities.User;
import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;

class InterfaceUrlTest {
  static ApplicationDatabase database;
  static MainActivity mainActivity;

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  InterfaceUrl interfaceWebsite;
  UserDataAccessObject userDataAccessObject;
  WebsiteDataAccessObject websiteDataAccessObject;
  UrlDataAccessObject urlDataAccessObject;
  String randomUser;
  String randomEmail;
  String randomLoginName;
  String randomMasterPassword;
  String randomWebsiteName;
  String randomUserPassword;
  String randomUrl;
  int maxLength;

  @AfterAll
  static void tearDown() throws Exception {
    mainActivity.deleteDatabase("testDatabase");
  }

  @BeforeEach
  public void setUp() throws Exception {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();
    }
    maxLength = 20;
    userDataAccessObject = database.getUserDataAccessObject();
    websiteDataAccessObject = database.getPasswordDataAccessObject();
    urlDataAccessObject = database.getWebsiteDataAccessObject();

    interfaceWebsite = new InterfaceUrl(urlDataAccessObject);

    randomUser = generateRandomString(maxLength);
    randomEmail = generateRandomString(maxLength) + "@email.de";
    randomLoginName = generateRandomString(maxLength);
    randomMasterPassword = generateRandomString(maxLength);
    randomWebsiteName = generateRandomString(maxLength) + ".de";
    randomUserPassword = generateRandomString(maxLength);
    randomUrl = generateRandomString(5) + "." + generateRandomString(maxLength) + ".com";
  }

  @AfterEach
  void clearDatabase() throws Exception {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  /** this method adds an User entity and Password entity into the DB */
  void initDB(
      String username,
      String email,
      String masterPassword,
      String websiteName,
      String loginName,
      String plainUserPassword) {
    userDataAccessObject.addUser(new User(username, email, masterPassword.getBytes()));
    websiteDataAccessObject.addWebsite(
        new Website(username, websiteName, loginName, plainUserPassword.getBytes()));
  }

  /**
   * this method checks if the given Url matches the Url of the given Website Entity specified by
   * username and websiteName
   */
  void checkExpectedDB(String username, String websiteName, String url) {
    Url expected = getWebsite(username, websiteName, url);

    assertTrue(expected != null);
    assertEquals(expected.username, username);
    assertEquals(expected.websiteName, websiteName);
    assertEquals(expected.webAddress, url);
  }

  /**
   * this method finds the saved Website entity given the username, websiteName and Url
   *
   * @return
   */
  Url getWebsite(String username, String websiteName, String url) {
    for (Url web : urlDataAccessObject.getUrlList(username, websiteName)) {
      if (web.webAddress.equals(url)) return web;
    }
    return null;
  }

  /**
   * this method adds a random amount (less than 20) of entity Website into the DB under the given
   * username and websiteName the added entries are also saved into the ArrayList
   */
  void addRandomWebsiteUrl(String username, String websiteName, ArrayList<Url> list) {
    String url;
    int amount = new Random().nextInt(20) + 1;
    for (int i = 0; i < amount; i++) {
      url = generateRandomString(maxLength) + ".com";
      Url toAdd = new Url(username, websiteName, url);
      list.add(toAdd);
      urlDataAccessObject.addUrl(toAdd);
    }
    list.sort(new WebsiteComparator());
  }

  @Nested
  @DisplayName("Tests for saveUrl")
  class saveUrlTest {

    @Test
    @DisplayName("Case: Success")
    void saveUrlSuccess() {
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          randomLoginName,
          randomUserPassword);
      assertTrue(interfaceWebsite.saveUrl(randomUser, randomWebsiteName, randomUrl));
      checkExpectedDB(randomUser, randomWebsiteName, randomUrl);
    }

    @Test
    @DisplayName("Case: Already Exist")
    void saveUrlExisted() {
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          randomLoginName,
          randomUserPassword);
      urlDataAccessObject.addUrl(new Url(randomUser, randomWebsiteName, randomUrl));
      assertFalse(interfaceWebsite.saveUrl(randomUser, randomWebsiteName, randomUrl));
      checkExpectedDB(randomUser, randomWebsiteName, randomUrl);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Website/saveUrlFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void saveUrlFailure(
        String displayCase,
        String userExistedInDB,
        String websiteExistedInDB,
        String userGiven,
        String websiteGiven,
        String urlGiven) {
      urlGiven = convertNullToEmptyString(urlGiven);
      initDB(
          userExistedInDB,
          randomEmail,
          randomMasterPassword,
          websiteExistedInDB,
          randomLoginName,
          randomUserPassword);
      assertFalse(interfaceWebsite.saveUrl(userGiven, websiteGiven, urlGiven));
      assertNull(getWebsite(userGiven, websiteGiven, urlGiven));
    }
  }

  @Nested
  @DisplayName("Tests for deleteUrl")
  class deleteUrlTest {

    @Test
    @DisplayName("Case: Success")
    void deleteUrlSuccess() {
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          randomLoginName,
          randomUserPassword);
      urlDataAccessObject.addUrl(new Url(randomUser, randomWebsiteName, randomUrl));
      assertTrue(interfaceWebsite.deleteUrl(randomUser, randomWebsiteName, randomUrl));
      assertNull(getWebsite(randomUser, randomWebsiteName, randomUrl));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Website/deleteUrlFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void deleteUrlFailure(
        String displayCase,
        String usernameExistedInDB,
        String websiteExistedInDB,
        String urlExistedInDB,
        String usernameGiven,
        String websiteGiven,
        String urlGiven) {
      initDB(
          usernameExistedInDB,
          randomEmail,
          randomMasterPassword,
          websiteExistedInDB,
          randomLoginName,
          randomUserPassword);
      urlDataAccessObject.addUrl(
          new Url(usernameExistedInDB, websiteExistedInDB, urlExistedInDB));
      assertFalse(interfaceWebsite.deleteUrl(usernameGiven, websiteGiven, urlGiven));
      checkExpectedDB(usernameExistedInDB, websiteExistedInDB, urlExistedInDB);
    }
  }

  @Nested
  @DisplayName("Test for getUrlList")
  class getUrlListTest {

    @Test
    @DisplayName("Case: Success")
    void getUrlListSuccess() {
      ArrayList<Url> list = new ArrayList<>();
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          randomLoginName,
          randomUserPassword);
      addRandomWebsiteUrl(randomUser, randomWebsiteName, list);
      assertEquals(
          "{\"dataArray\":" + list + "}",
          interfaceWebsite.getUrlList(randomUser, randomWebsiteName));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Website/getUrlListFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void getUrlListFailure(
        String displayCase,
        String usernameExistedInDB,
        String websiteExistedInDB,
        String usernameGiven,
        String websiteGiven) {
      ArrayList<Url> list = new ArrayList<>();
      initDB(
          usernameExistedInDB,
          randomEmail,
          randomMasterPassword,
          websiteExistedInDB,
          randomLoginName,
          randomUserPassword);
      addRandomWebsiteUrl(usernameExistedInDB, websiteExistedInDB, list);
      assertNotEquals(
          "{\"dataArray\":" + list + "}", interfaceWebsite.getUrlList(usernameGiven, websiteGiven));
      assertEquals("{\"dataArray\":[]}", interfaceWebsite.getUrlList(usernameGiven, websiteGiven));
    }
  }

  class WebsiteComparator implements java.util.Comparator<Url> {
    @Override
    public int compare(Url url1, Url url2) {
      return url1.webAddress.compareTo(url2.webAddress);
    }
  }
}
