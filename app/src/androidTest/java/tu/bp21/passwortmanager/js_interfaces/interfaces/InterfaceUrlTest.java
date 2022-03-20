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
  String randomUserPassword;
  String randomWebsiteName;
  String randomLoginPassword;
  String randomWebAddress;
  static final int stringMaxLength = 20;

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
    userDataAccessObject = database.getUserDataAccessObject();
    websiteDataAccessObject = database.getPasswordDataAccessObject();
    urlDataAccessObject = database.getWebsiteDataAccessObject();

    interfaceWebsite = new InterfaceUrl(urlDataAccessObject);

    randomUser = generateRandomString(stringMaxLength);
    randomEmail = generateRandomString(stringMaxLength) + "@email.de";
    randomLoginName = generateRandomString(stringMaxLength);
    randomUserPassword = generateRandomString(stringMaxLength);
    randomWebsiteName = generateRandomString(stringMaxLength);
    randomLoginPassword = generateRandomString(stringMaxLength);
    randomWebAddress = generateRandomString(stringMaxLength) + "." + generateRandomString(stringMaxLength) + ".com";
  }

  @AfterEach
  void clearDatabase() throws Exception {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  /** this method adds an User entity and Website entity into the DB */
  void initDB(
      String username,
      String email,
      String userPassword,
      String websiteName,
      String loginName,
      String plainLoginPassword) {
    userDataAccessObject.addUser(new User(username, email, userPassword.getBytes()));
    websiteDataAccessObject.addWebsite(
        new Website(username, websiteName, loginName, plainLoginPassword.getBytes()));
  }

  /**
   * this method checks if the given Url matches the Url of the given Website Entity specified by
   * username and websiteName
   */
  void checkExpectedDB(String username, String websiteName, String webAddress) {
    Url expected = getWebsite(username, websiteName, webAddress);

    assertTrue(expected != null);
    assertEquals(expected.username, username);
    assertEquals(expected.websiteName, websiteName);
    assertEquals(expected.webAddress, webAddress);
  }

  /**
   * this method finds the saved Url entity given the username, websiteName and Url
   */
  Url getWebsite(String username, String websiteName, String url) {
    for (Url web : urlDataAccessObject.getUrlList(username, websiteName)) {
      if (web.webAddress.equals(url)) return web;
    }
    return null;
  }

  /**
   * this method adds a random amount (less than 20) of entity Url into the DB under the given
   * username and websiteName the added entries are also saved into the ArrayList
   */
  void addRandomWebsiteUrl(String username, String websiteName, ArrayList<Url> list) {
    String url;
    int amount = new Random().nextInt(20) + 1;
    for (int i = 0; i < amount; i++) {
      url = generateRandomString(stringMaxLength) + ".com";
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
              randomUserPassword,
          randomWebsiteName,
          randomLoginName,
              randomLoginPassword);
      assertTrue(interfaceWebsite.saveUrl(randomUser, randomWebsiteName, randomWebAddress));
      checkExpectedDB(randomUser, randomWebsiteName, randomWebAddress);
    }

    @Test
    @DisplayName("Case: Already Exist")
    void saveUrlExisted() {
      initDB(
          randomUser,
          randomEmail,
              randomUserPassword,
          randomWebsiteName,
          randomLoginName,
              randomLoginPassword);
      urlDataAccessObject.addUrl(new Url(randomUser, randomWebsiteName, randomWebAddress));
      assertFalse(interfaceWebsite.saveUrl(randomUser, randomWebsiteName, randomWebAddress));
      checkExpectedDB(randomUser, randomWebsiteName, randomWebAddress);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceUrlTest/saveUrlFailure.csv", numLinesToSkip = 1)
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
              randomUserPassword,
          websiteExistedInDB,
          randomLoginName,
              randomLoginPassword);
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
              randomUserPassword,
          randomWebsiteName,
          randomLoginName,
              randomLoginPassword);
      urlDataAccessObject.addUrl(new Url(randomUser, randomWebsiteName, randomWebAddress));
      assertTrue(interfaceWebsite.deleteUrl(randomUser, randomWebsiteName, randomWebAddress));
      assertNull(getWebsite(randomUser, randomWebsiteName, randomWebAddress));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceUrlTest/deleteUrlFailure.csv", numLinesToSkip = 1)
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
              randomUserPassword,
          websiteExistedInDB,
          randomLoginName,
              randomLoginPassword);
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
              randomUserPassword,
          randomWebsiteName,
          randomLoginName,
              randomLoginPassword);
      addRandomWebsiteUrl(randomUser, randomWebsiteName, list);
      assertEquals(
          "{\"dataArray\":" + list + "}",
          interfaceWebsite.getUrlList(randomUser, randomWebsiteName));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceUrlTest/getUrlListFailure.csv", numLinesToSkip = 1)
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
              randomUserPassword,
          websiteExistedInDB,
          randomLoginName,
              randomLoginPassword);
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
