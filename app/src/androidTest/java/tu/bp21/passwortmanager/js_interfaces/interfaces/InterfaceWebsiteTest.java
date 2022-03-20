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
  String randomMasterPassword;
  String randomWebsiteName;
  String randomUserPassword;
  byte[] key;
  String keyAsHex;
  int maxLength;

  @AfterAll
  static void tearDown() throws Exception {
    mainActivity.deleteDatabase("testDatabase");
  }

  @BeforeEach
  void setUp() throws Exception {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();
    }
    maxLength = 100;
    userDataAccessObject = database.getUserDataAccessObject();
    websiteDataAccessObject = database.getPasswordDataAccessObject();

    interfaceWebsite = new InterfaceWebsite(websiteDataAccessObject);

    randomUser = generateRandomString(maxLength);
    randomEmail = generateRandomString(maxLength) + "@email.de";
    randomLoginName = generateRandomString(maxLength);
    randomMasterPassword = generateRandomString(maxLength);
    randomWebsiteName = generateRandomString(maxLength) + ".de";
    randomUserPassword = generateRandomString(maxLength);
  }

  @AfterEach
  void clearDatabase() throws Exception {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  @Test
  void getPasswordListTest() {
    String expectedUser = randomUser;
    String differentUser = generateRandomString(maxLength);
    while (differentUser.equals(expectedUser)) differentUser = generateRandomString(maxLength);

    userDataAccessObject.addUser(
        new User(expectedUser, randomEmail, randomMasterPassword.getBytes()));
    ArrayList<Website> list = new ArrayList<>();
    addRandomPassword(expectedUser, list);
    assertEquals(
        "{\"dataArray\":" + list + "}", interfaceWebsite.getWebsiteOverviewList(expectedUser));
    assertNotEquals(
        "{\"dataArray\":" + list + "}", interfaceWebsite.getWebsiteOverviewList(differentUser));
    assertEquals("{\"dataArray\":[]}", interfaceWebsite.getWebsiteOverviewList(differentUser));
  }

  /**
   * this method adds a random amount (less than 30) of entity Password into the DB under the given
   * username the added entries are also saved into the ArrayList
   */
  void addRandomPassword(String username, ArrayList<Website> list) {
    String websiteName, plainUserPassword, loginName;
    int amount = new Random().nextInt(30) + 1;
    for (int i = 0; i < amount; i++) {
      websiteName = generateRandomString(maxLength) + ".com";
      plainUserPassword = generateRandomString(maxLength);
      loginName = generateRandomString(maxLength);
      Website websiteEntitytoAdd =
          new Website(username, websiteName, loginName, plainUserPassword.getBytes());
      list.add(websiteEntitytoAdd);
      websiteDataAccessObject.addWebsite(websiteEntitytoAdd);
    }
    list.sort(new PasswordComparator());
  }

  /**
   * this method adds an User entity and Password entity into the DB, also generate the key for
   * crypto functions
   */
  void initDB(
      String username,
      String email,
      String masterPassword,
      String websiteName,
      String loginName,
      String plainUserPassword) {
    byte[] salt = Crypto.generateSecureByteArray(16);
    byte[] associatedData = (username + websiteName).getBytes();
    key = Crypto.generateKey(masterPassword, salt);
    keyAsHex = BaseEncoding.base16().encode(key);
    userDataAccessObject.addUser(new User(username, email, masterPassword.getBytes()));
    websiteDataAccessObject.addWebsite(
        new Website(
            username,
            websiteName,
            loginName,
            Crypto.encrypt(
                plainUserPassword, associatedData, key, Crypto.generateUniqueIV(null, 12))));
  }

  /**
   * this method checks if the given loginName and plainUserPassword matches the loginName and
   * plainUserPassword of the given Entity specified by username and websiteName
   */
  void checkExpectedDB(
      String username, String websiteName, String loginName, String plainUserPassword, byte[] key) {
    Website expected = websiteDataAccessObject.getWebsite(username, websiteName);
    byte[] associatedData = (username + websiteName).getBytes();
    assertTrue(expected != null);
    assertEquals(expected.loginName, loginName);
    assertEquals(
        Crypto.decrypt(expected.encryptedLoginPassword, associatedData, key), plainUserPassword);
  }

  @Nested
  @DisplayName("Test for createPassword")
  class createWebsiteTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/createPasswordSuccess.csv", numLinesToSkip = 1)
    @DisplayName("Case: Success")
    void createPasswordSuccess(String loginName, String plainUserPassword) {
      if (loginName != null) loginName = randomLoginName;
      if (plainUserPassword != null) plainUserPassword = randomUserPassword;
      loginName = convertNullToEmptyString(loginName);
      plainUserPassword = convertNullToEmptyString(plainUserPassword);
      userDataAccessObject.addUser(
          new User(randomUser, randomEmail, randomMasterPassword.getBytes()));
      byte[] salt = Crypto.generateSecureByteArray(16);
      key = Crypto.generateKey(randomMasterPassword, salt);
      boolean worked =
          interfaceWebsite.createWebsite(
              randomUser,
              randomWebsiteName,
              loginName,
              plainUserPassword,
              BaseEncoding.base16().encode(key));
      assertTrue(worked);
      checkExpectedDB(randomUser, randomWebsiteName, loginName, plainUserPassword, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/createPasswordExisted.csv", numLinesToSkip = 1)
    @DisplayName("Case: Existed")
    void createPasswordExisted(
        String displayCase,
        String loginName1,
        String plainUserPassword1,
        String loginName2,
        String plainUserPassword2) {
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          loginName1,
          plainUserPassword1);
      boolean worked =
          interfaceWebsite.createWebsite(
              randomUser, randomWebsiteName, loginName2, plainUserPassword2, keyAsHex);
      assertFalse(worked);
      checkExpectedDB(randomUser, randomWebsiteName, loginName1, plainUserPassword1, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/createPasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void createPasswordFailure(
        String displayCase, String userExistedInDB, String userToCreate, String websiteToCreate) {
      websiteToCreate = convertNullToEmptyString(websiteToCreate);
      userDataAccessObject.addUser(
          new User(userExistedInDB, randomEmail, randomMasterPassword.getBytes()));
      byte[] salt = Crypto.generateSecureByteArray(16);
      key = Crypto.generateKey(randomMasterPassword, salt);
      boolean worked =
          interfaceWebsite.createWebsite(
              userToCreate,
              websiteToCreate,
              randomLoginName,
              randomUserPassword,
              BaseEncoding.base16().encode(key));
      assertFalse(worked);
      assertNull(websiteDataAccessObject.getWebsite(userToCreate, websiteToCreate));
    }
  }

  @Nested
  @DisplayName("Tests for updatePassword")
  class updateWebsiteTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/updatePasswordSuccess.csv", numLinesToSkip = 1)
    @DisplayName("Case: Success")
    void updatePasswordSuccess(
        String displayCase,
        String username,
        String loginName,
        String plainUserPassword,
        String newLoginName,
        String newPlainUserPassword) {
      initDB(
          username,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          loginName,
          plainUserPassword);

      assertTrue(
          interfaceWebsite.updateWebsite(
              username, randomWebsiteName, newLoginName, newPlainUserPassword, keyAsHex));

      checkExpectedDB(username, randomWebsiteName, newLoginName, newPlainUserPassword, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/updatePasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void updatePasswordFailure(
        String displayCase,
        String username1,
        String websiteName1,
        String loginName1,
        String plainUserPassword1,
        String username2,
        String websiteName2,
        String loginName2,
        String plainUserPassword2) {
      initDB(
          username1,
          randomEmail,
          randomMasterPassword,
          websiteName1,
          loginName1,
          plainUserPassword1);

      assertFalse(
          interfaceWebsite.updateWebsite(
              username2, websiteName2, loginName2, plainUserPassword2, keyAsHex));

      checkExpectedDB(username1, websiteName1, loginName1, plainUserPassword1, key);
    }
  }

  @Nested
  @DisplayName("Tests for deletePassword")
  class deleteWebsiteTest {

    @Test
    @DisplayName("Case: Success")
    void deletePasswordSuccess() {
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          randomLoginName,
          randomUserPassword);
      String randomUrl = generateRandomString(maxLength);
      UrlDataAccessObject urlDataAccessObject = database.getWebsiteDataAccessObject();
      urlDataAccessObject.addUrl(new Url(randomUser, randomWebsiteName, randomUrl));

      assertTrue(interfaceWebsite.deleteWebsite(randomUser, randomWebsiteName));
      assertNull(websiteDataAccessObject.getWebsite(randomUser, randomWebsiteName));
      assertTrue(urlDataAccessObject.getUrlList(randomUser, randomWebsiteName).isEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/deletePasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void deletePasswordFailure(
        String displayCase,
        String username1,
        String websiteName1,
        String username2,
        String websiteName2) {
      initDB(
          username1,
          randomEmail,
          randomMasterPassword,
          websiteName1,
          randomLoginName,
          randomUserPassword);

      assertFalse(interfaceWebsite.deleteWebsite(username2, websiteName2));

      checkExpectedDB(username1, websiteName1, randomLoginName, randomUserPassword, key);
    }
  }

  @Nested
  @DisplayName("Tests for getLoginName")
  class getLoginNameTest {

    @Test
    @DisplayName("Case: Standard")
    void getLoginNameStandard() {
      String expectedLoginName = randomLoginName;
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          expectedLoginName,
          randomUserPassword);
      String actualLoginName = interfaceWebsite.getLoginName(randomUser, randomWebsiteName);
      assertEquals(expectedLoginName, actualLoginName);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/getLoginNameFailure.csv", numLinesToSkip = 1)
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
          randomMasterPassword,
          websiteName,
          randomLoginName,
          randomUserPassword);
      String actualLoginName = interfaceWebsite.getLoginName(actualUserName, actualWebsiteName);
      assertEquals(expectedLoginName, actualLoginName);
    }
  }

  @Nested
  @DisplayName("Tests for getPassword")
  class getWebsiteTest {

    @Test
    @DisplayName("Case: Standard")
    void getPasswordStandard() {
      String expectedPassword = randomUserPassword;
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsiteName,
          randomLoginName,
          expectedPassword);
      String actualPassword =
          interfaceWebsite.getLoginPassword(randomUser, randomWebsiteName, keyAsHex);
      assertEquals(expectedPassword, actualPassword);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/getPasswordFailure.csv", numLinesToSkip = 1)
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
          randomMasterPassword,
          websiteName,
          randomLoginName,
          randomUserPassword);
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
