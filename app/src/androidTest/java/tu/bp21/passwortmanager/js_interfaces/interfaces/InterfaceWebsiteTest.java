package tu.bp21.passwortmanager.js_interfaces.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.convertNullToEmptyString;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;
import java.util.Random;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.Constants;
import tu.bp21.passwortmanager.cryptography.Crypto;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.data_access_objects.UrlDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import tu.bp21.passwortmanager.db.data_access_objects.UserDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Website;
import tu.bp21.passwortmanager.db.entities.User;
import tu.bp21.passwortmanager.db.entities.Url;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName(value = "Tests for InterfacePassword")
class InterfaceWebsiteTest {
  static ApplicationDatabase database;
  static MainActivity mainActivity;

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  InterfaceWebsite interfaceWebsite;
  UserDataAccessObject userDataAccessObject;
  WebsiteDataAccessObject websiteDataAccessObject;
  String randomUser;
  String randomEmail;
  String randomLoginName;
  String randomUserPassword;
  String randomWebsiteName;
  String randomLoginPassword;
  byte[] key;
  String keyAsHex;
  static final int stringMaxLength = 100;
  static final int saltSize = Constants.SALT_LENGTH;
  static final int ivSize = Constants.ENCRYPT_IV_LENGTH;

  @AfterAll
  static void tearDown() throws Exception {
    mainActivity.deleteDatabase("testDatabase");
  }

  @BeforeEach
  void setUp(){
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

    interfaceWebsite = new InterfaceWebsite(websiteDataAccessObject);

    randomUser = generateRandomString(stringMaxLength);
    randomEmail = generateRandomString(stringMaxLength) + "@email.de";
    randomLoginName = generateRandomString(stringMaxLength);
    randomUserPassword = generateRandomString(stringMaxLength);
    randomWebsiteName = generateRandomString(stringMaxLength);
    randomLoginPassword = generateRandomString(stringMaxLength);
  }

  @AfterEach
  void clearDatabase() throws Exception {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  @Test
  void getPasswordListTest() {
    String expectedUser = randomUser;
    String differentUser = generateRandomString(stringMaxLength);
    while (differentUser.equals(expectedUser))
      differentUser = generateRandomString(stringMaxLength);

    userDataAccessObject.addUser(
        new User(expectedUser, randomEmail, randomUserPassword.getBytes()));
    ArrayList<Website> list = new ArrayList<>();
    addRandomWebsite(expectedUser, list);
    assertEquals(
        "{\"dataArray\":" + list + "}", interfaceWebsite.getWebsiteOverviewList(expectedUser));
    assertNotEquals(
        "{\"dataArray\":" + list + "}", interfaceWebsite.getWebsiteOverviewList(differentUser));
    assertEquals("{\"dataArray\":[]}", interfaceWebsite.getWebsiteOverviewList(differentUser));
  }

  /**
   * this method adds a random amount (less than 30) of entity Website into the DB under the given
   * username the added entries are also saved into the ArrayList
   */
  void addRandomWebsite(String username, ArrayList<Website> list) {
    String websiteName, plainUserPassword, loginName;
    int amount = new Random().nextInt(30) + 1;
    for (int i = 0; i < amount; i++) {
      websiteName = generateRandomString(stringMaxLength) + ".com";
      plainUserPassword = generateRandomString(stringMaxLength);
      loginName = generateRandomString(stringMaxLength);
      Website websiteEntitytoAdd =
          new Website(username, websiteName, loginName, plainUserPassword.getBytes());
      list.add(websiteEntitytoAdd);
      websiteDataAccessObject.addWebsite(websiteEntitytoAdd);
    }
    list.sort(new PasswordComparator());
  }

  /**
   * this method adds an User entity and Website entity into the DB, also generate the key for
   * crypto functions
   */
  void initDB(
      String username,
      String email,
      String userPassword,
      String websiteName,
      String loginName,
      String plainLoginPassword) {
    byte[] salt = Crypto.generateSecureByteArray(saltSize);
    byte[] associatedData = (username + websiteName).getBytes();
    key = Crypto.generateKey(userPassword, salt);
    keyAsHex = BaseEncoding.base16().encode(key);
    userDataAccessObject.addUser(new User(username, email, userPassword.getBytes()));
    websiteDataAccessObject.addWebsite(
        new Website(
            username,
            websiteName,
            loginName,
            Crypto.encrypt(
                plainLoginPassword, associatedData, key, Crypto.generateUniqueIV(null, ivSize))));
  }

  /**
   * this method checks if the given loginName and plainLoginPassword matches the loginName and
   * plainLoginPassword of the given Entity specified by username and websiteName
   */
  void checkExpectedDB(
      String username,
      String websiteName,
      String loginName,
      String plainLoginPassword,
      byte[] key) {
    Website expected = websiteDataAccessObject.getWebsite(username, websiteName);
    byte[] associatedData = (username + websiteName).getBytes();
    assertTrue(expected != null);
    assertEquals(expected.loginName, loginName);
    assertEquals(
        Crypto.decrypt(expected.encryptedLoginPassword, associatedData, key), plainLoginPassword);
  }

  @Nested
  @DisplayName("Test for createWebsite")
  class CreateWebsiteTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/createWebsiteSuccess.csv", numLinesToSkip = 1)
    @DisplayName("Case: Success")
    void createWebsiteSuccess(String loginName, String plainLoginPassword) {
      if (loginName != null) loginName = randomLoginName;
      if (plainLoginPassword != null) plainLoginPassword = randomLoginPassword;
      loginName = convertNullToEmptyString(loginName);
      plainLoginPassword = convertNullToEmptyString(plainLoginPassword);
      userDataAccessObject.addUser(
          new User(randomUser, randomEmail, randomUserPassword.getBytes()));
      byte[] salt = Crypto.generateSecureByteArray(saltSize);
      key = Crypto.generateKey(randomUserPassword, salt);
      boolean worked =
          interfaceWebsite.createWebsite(
              randomUser,
              randomWebsiteName,
              loginName,
              plainLoginPassword,
              BaseEncoding.base16().encode(key));
      assertTrue(worked);
      checkExpectedDB(randomUser, randomWebsiteName, loginName, plainLoginPassword, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/createWebsiteExisted.csv", numLinesToSkip = 1)
    @DisplayName("Case: Existed")
    void createWebsiteExisted(
        String displayCase,
        String loginName1,
        String plainLoginPassword1,
        String loginName2,
        String plainLoginPassword2) {
      initDB(
          randomUser,
          randomEmail,
          randomUserPassword,
          randomWebsiteName,
          loginName1,
          plainLoginPassword1);
      boolean worked =
          interfaceWebsite.createWebsite(
              randomUser, randomWebsiteName, loginName2, plainLoginPassword2, keyAsHex);
      assertFalse(worked);
      checkExpectedDB(randomUser, randomWebsiteName, loginName1, plainLoginPassword1, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/createWebsiteFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void createWebsiteFailure(
        String displayCase, String userExistedInDB, String userToCreate, String websiteToCreate) {
      websiteToCreate = convertNullToEmptyString(websiteToCreate);
      userDataAccessObject.addUser(
          new User(userExistedInDB, randomEmail, randomUserPassword.getBytes()));
      byte[] salt = Crypto.generateSecureByteArray(saltSize);
      key = Crypto.generateKey(randomUserPassword, salt);
      boolean worked =
          interfaceWebsite.createWebsite(
              userToCreate,
              websiteToCreate,
              randomLoginName,
              randomLoginPassword,
              BaseEncoding.base16().encode(key));
      assertFalse(worked);
      assertNull(websiteDataAccessObject.getWebsite(userToCreate, websiteToCreate));
    }
  }

  @Nested
  @DisplayName("Tests for updateWebsite")
  class UpdateWebsiteTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/updateWebsiteSuccess.csv", numLinesToSkip = 1)
    @DisplayName("Case: Success")
    void updateWebsiteSuccess(
        String displayCase,
        String username,
        String loginName,
        String plainLoginPassword,
        String newLoginName,
        String newPlainLoginPassword) {
      initDB(
          username,
          randomEmail,
          randomUserPassword,
          randomWebsiteName,
          loginName,
          plainLoginPassword);

      assertTrue(
          interfaceWebsite.updateWebsite(
              username, randomWebsiteName, newLoginName, newPlainLoginPassword, keyAsHex));

      checkExpectedDB(username, randomWebsiteName, newLoginName, newPlainLoginPassword, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/updateWebsiteFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void updatePasswordFailure(
        String displayCase,
        String username1,
        String websiteName1,
        String loginName1,
        String plainLoginPassword1,
        String username2,
        String websiteName2,
        String loginName2,
        String plainLoginPassword2) {
      initDB(
          username1,
          randomEmail,
          randomUserPassword,
          websiteName1,
          loginName1,
          plainLoginPassword1);

      assertFalse(
          interfaceWebsite.updateWebsite(
              username2, websiteName2, loginName2, plainLoginPassword2, keyAsHex));

      checkExpectedDB(username1, websiteName1, loginName1, plainLoginPassword1, key);
    }
  }

  @Nested
  @DisplayName("Tests for deleteWebsite")
  class DeleteWebsiteTest {

    @Test
    @DisplayName("Case: Success")
    void deleteWebsiteSuccess() {
      initDB(
          randomUser,
          randomEmail,
          randomUserPassword,
          randomWebsiteName,
          randomLoginName,
          randomLoginPassword);
      String randomUrl = generateRandomString(stringMaxLength);
      UrlDataAccessObject urlDataAccessObject = database.getWebsiteDataAccessObject();
      urlDataAccessObject.addUrl(new Url(randomUser, randomWebsiteName, randomUrl));

      assertTrue(interfaceWebsite.deleteWebsite(randomUser, randomWebsiteName));
      assertNull(websiteDataAccessObject.getWebsite(randomUser, randomWebsiteName));
      assertTrue(urlDataAccessObject.getUrlList(randomUser, randomWebsiteName).isEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/deleteWebsiteFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void deleteWebsiteFailure(
        String displayCase,
        String username1,
        String websiteName1,
        String username2,
        String websiteName2) {
      initDB(
          username1,
          randomEmail,
          randomUserPassword,
          websiteName1,
          randomLoginName,
          randomLoginPassword);

      assertFalse(interfaceWebsite.deleteWebsite(username2, websiteName2));

      checkExpectedDB(username1, websiteName1, randomLoginName, randomLoginPassword, key);
    }
  }

  @Nested
  @DisplayName("Tests for getLoginName")
  class GetLoginNameTest {

    @Test
    @DisplayName("Case: Standard")
    void getLoginNameStandard() {
      String expectedLoginName = randomLoginName;
      initDB(
          randomUser,
          randomEmail,
          randomUserPassword,
          randomWebsiteName,
          expectedLoginName,
          randomLoginPassword);
      String actualLoginName = interfaceWebsite.getLoginName(randomUser, randomWebsiteName);
      assertEquals(expectedLoginName, actualLoginName);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/getLoginNameFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void getLoginNameFailure(
        String displayCase,
        String username,
        String websiteName,
        String actualUserName,
        String actualWebsiteName) {
      String expectedLoginName = "";
      initDB(
          username,
          randomEmail,
          randomUserPassword,
          websiteName,
          randomLoginName,
          randomLoginPassword);
      String actualLoginName = interfaceWebsite.getLoginName(actualUserName, actualWebsiteName);
      assertEquals(expectedLoginName, actualLoginName);
    }
  }

  @Nested
  @DisplayName("Tests for getWebsite")
  class GetWebsiteTest {

    @Test
    @DisplayName("Case: Standard")
    void getWebsiteStandard() {
      String expectedPassword = randomLoginPassword;
      initDB(
          randomUser,
          randomEmail,
          randomUserPassword,
          randomWebsiteName,
          randomLoginName,
          expectedPassword);
      String actualPassword =
          interfaceWebsite.getLoginPassword(randomUser, randomWebsiteName, keyAsHex);
      assertEquals(expectedPassword, actualPassword);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceWebsiteTest/getWebsiteFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void getPasswordFailure(
        String displayCase,
        String username,
        String websiteName,
        String actualUserName,
        String actualWebsiteName) {
      String expectedPassword = "";
      initDB(
          username,
          randomEmail,
          randomUserPassword,
          websiteName,
          randomLoginName,
          randomLoginPassword);
      String actualPassword =
          interfaceWebsite.getLoginPassword(actualUserName, actualWebsiteName, keyAsHex);
      assertEquals(expectedPassword, actualPassword);
    }
  }

  class PasswordComparator implements java.util.Comparator<Website> {
    @Override
    public int compare(Website websiteEntity1, Website websiteEntity2) {
      return websiteEntity1.websiteName.compareTo(websiteEntity2.websiteName);
    }
  }
}
