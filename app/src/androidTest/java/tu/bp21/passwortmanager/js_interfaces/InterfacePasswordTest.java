package tu.bp21.passwortmanager.js_interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tu.bp21.passwortmanager.StringFunction.convertNullToEmptyString;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;
import java.util.Random;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.ApplicationDatabase;
import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.Website;
import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.dao.UserDao;
import tu.bp21.passwortmanager.db.dao.WebsiteDao;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName(value = "Tests for InterfacePassword")
class InterfacePasswordTest {
  static ApplicationDatabase database;
  static MainActivity mainActivity;

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  InterfacePassword interfacePassword;
  UserDao userDao;
  PasswordDao passwordDao;
  String randomUser;
  String randomEmail;
  String randomLoginName;
  String randomMasterPassword;
  String randomWebsite;
  String randomPassword;
  byte[] key;
  String keyAsHex;

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
    userDao = database.getUserDao();
    passwordDao = database.getPasswordDao();

    interfacePassword = new InterfacePassword(passwordDao);

    randomUser = generateRandomString(20);
    randomEmail = generateRandomString(20) + "@email.de";
    randomLoginName = generateRandomString(20);
    randomMasterPassword = generateRandomString(20);
    randomWebsite = generateRandomString(20) + ".de";
    randomPassword = generateRandomString(20);
  }

  @AfterEach
  void clearDatabase() throws Exception {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  @Test
  void getPasswordListTest() {
    String expectedUser = randomUser;
    String differentUser = generateRandomString(20);
    while (differentUser.equals(expectedUser)) differentUser = generateRandomString(20);

    userDao.addUser(new User(expectedUser, randomEmail, randomMasterPassword.getBytes()));
    ArrayList<Password> list = new ArrayList<>();
    addRandomPassword(expectedUser, list);
    assertEquals(
        "{\"dataArray\":" + list + "}",
        interfacePassword.getPasswordList(expectedUser, randomMasterPassword));
    assertNotEquals(
        "{\"dataArray\":" + list + "}",
        interfacePassword.getPasswordList(differentUser, randomMasterPassword));
    assertEquals(
        "{\"dataArray\":[]}",
        interfacePassword.getPasswordList(differentUser, randomMasterPassword));
  }

  /**
   * this method adds a random amount (less than 30) of entity Password into the DB under the given
   * username the added entries are also saved into the ArrayList
   */
  void addRandomPassword(String username, ArrayList<Password> list) {
    String website, password, loginName;
    int amount = new Random().nextInt(30) + 1;
    for (int i = 0; i < amount; i++) {
      website = generateRandomString(20) + ".com";
      password = generateRandomString(20);
      loginName = generateRandomString(20);
      Password toAdd = new Password(username, website, loginName, password.getBytes());
      list.add(toAdd);
      passwordDao.addPassword(toAdd);
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
      String website,
      String loginName,
      String password) {
    byte[] salt = Crypto.generateSecureByteArray(16);
    byte[] associatedData = (username + website).getBytes();
    key = Crypto.generateKey(masterPassword, salt);
    keyAsHex = BaseEncoding.base16().encode(key);
    userDao.addUser(new User(username, email, masterPassword.getBytes()));
    passwordDao.addPassword(
        new Password(
            username,
            website,
            loginName,
            Crypto.encrypt(password, associatedData, key, Crypto.generateUniqueIV(null, 12))));
  }

  /**
   * this method checks if the given loginName and password matches the loginName and password of
   * the given Entity specified by username and website
   */
  void checkExpectedDB(
      String username, String website, String loginName, String password, byte[] key) {
    Password expected = passwordDao.getPassword(username, website);
    byte[] associatedData = (username + website).getBytes();
    assertTrue(expected != null);
    assertEquals(expected.loginName, loginName);
    assertEquals(Crypto.decrypt(expected.password, associatedData, key), password);
  }

  @Nested
  @DisplayName("Test for createPassword")
  class createPasswordTest {

    @Test
    @DisplayName("Case: Success")
    void createPasswordSuccess() {
      userDao.addUser(new User(randomUser, randomEmail, randomMasterPassword.getBytes()));
      byte[] salt = Crypto.generateSecureByteArray(16);
      key = Crypto.generateKey(randomMasterPassword, salt);
      boolean worked =
          interfacePassword.createPassword(
              randomUser,
              randomWebsite,
              randomLoginName,
              randomPassword,
              BaseEncoding.base16().encode(key));
      assertTrue(worked);
      checkExpectedDB(randomUser, randomWebsite, randomLoginName, randomPassword, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/createPasswordExisted.csv", numLinesToSkip = 1)
    @DisplayName("Case: Existed")
    void createPasswordExisted(
        String displayCase,
        String loginName1,
        String password1,
        String loginName2,
        String password2) {
      initDB(randomUser, randomEmail, randomMasterPassword, randomWebsite, loginName1, password1);
      boolean worked =
          interfacePassword.createPassword(
              randomUser, randomWebsite, loginName2, password2, keyAsHex);
      assertFalse(worked);
      checkExpectedDB(randomUser, randomWebsite, loginName1, password1, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/createPasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void createPasswordFailure(
        String displayCase,
        String userExistedInDB,
        String userToCreate,
        String websiteToCreate,
        String loginNameToCreate,
        String passwordToCreate) {
      userToCreate = convertNullToEmptyString(userToCreate);
      websiteToCreate = convertNullToEmptyString(websiteToCreate);
      loginNameToCreate = convertNullToEmptyString(loginNameToCreate);
      passwordToCreate = convertNullToEmptyString(passwordToCreate);
      userDao.addUser(new User(userExistedInDB, randomEmail, randomMasterPassword.getBytes()));
      byte[] salt = Crypto.generateSecureByteArray(16);
      key = Crypto.generateKey(randomMasterPassword, salt);
      boolean worked =
          interfacePassword.createPassword(
              userToCreate,
              websiteToCreate,
              loginNameToCreate,
              passwordToCreate,
              BaseEncoding.base16().encode(key));
      assertFalse(worked);
      assertNull(passwordDao.getPassword(userToCreate, websiteToCreate));
    }
  }

  @Nested
  @DisplayName("Tests for updatePassword")
  class updatePasswordTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/updatePasswordSuccess.csv", numLinesToSkip = 1)
    @DisplayName("Case: Success")
    void updatePasswordSuccess(
        String displayCase,
        String username,
        String loginName,
        String password,
        String newLoginName,
        String newPassword) {
      initDB(username, randomEmail, randomMasterPassword, randomWebsite, loginName, password);

      assertTrue(
          interfacePassword.updatePassword(
              username, randomWebsite, newLoginName, newPassword, keyAsHex));

      checkExpectedDB(username, randomWebsite, newLoginName, newPassword, key);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/updatePasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void updatePasswordFailure(
        String displayCase,
        String username1,
        String website1,
        String loginName1,
        String password1,
        String username2,
        String website2,
        String loginName2,
        String password2) {
      initDB(username1, randomEmail, randomMasterPassword, website1, loginName1, password1);

      assertFalse(
          interfacePassword.updatePassword(username2, website2, loginName2, password2, keyAsHex));

      checkExpectedDB(username1, website1, loginName1, password1, key);
    }
  }

  @Nested
  @DisplayName("Tests for deletePassword")
  class deletePasswordTest {

    @Test
    @DisplayName("Case: Success")
    void deletePasswordSuccess() {
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsite,
          randomLoginName,
          randomPassword);
      String randomUrl = generateRandomString(20);
      WebsiteDao websiteDao = database.getWebsiteDao();
      websiteDao.addWebsite(new Website(randomUser, randomWebsite, randomUrl));

      assertTrue(interfacePassword.deletePassword(randomUser, randomWebsite));
      assertNull(passwordDao.getPassword(randomUser, randomWebsite));
      assertTrue(websiteDao.getWebsiteList(randomUser, randomWebsite).isEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/deletePasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void deletePasswordFailure(
        String displayCase, String username1, String website1, String username2, String website2) {
      initDB(
          username1, randomEmail, randomMasterPassword, website1, randomLoginName, randomPassword);

      assertFalse(interfacePassword.deletePassword(username2, website2));

      checkExpectedDB(username1, website1, randomLoginName, randomPassword, key);
    }
  }

  @Nested
  @DisplayName("Tests for getLoginName")
  class getLoginNameTest {

    @Test
    @DisplayName("Case: Success")
    void getLoginNameSuccess() {
      String expectedLoginName = randomLoginName;
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsite,
          expectedLoginName,
          randomPassword);
      String actualLoginName =
          interfacePassword.getLoginName(randomUser, randomMasterPassword, randomWebsite);
      assertEquals(expectedLoginName, actualLoginName);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/getLoginNameFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void getLoginNameFailure(
        String displayCase,
        String username,
        String masterPassword,
        String website,
        String actualUserName,
        String actualMasterPassword,
        String actualWebsite) {
      String expectedLoginName = "";
      initDB(username, randomEmail, masterPassword, website, randomLoginName, randomPassword);
      String actualLoginName =
          interfacePassword.getLoginName(actualUserName, actualMasterPassword, actualWebsite);
      assertEquals(expectedLoginName, actualLoginName);
    }
  }

  @Nested
  @DisplayName("Tests for getPassword")
  class getPasswordTest {

    @Test
    @DisplayName("Case: Success")
    void getPasswordSuccess() {
      String expectedPassword = randomPassword;
      initDB(
          randomUser,
          randomEmail,
          randomMasterPassword,
          randomWebsite,
          randomLoginName,
          expectedPassword);
      String actualPassword =
          interfacePassword.getPassword(randomUser, randomMasterPassword, randomWebsite, keyAsHex);
      assertEquals(expectedPassword, actualPassword);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/getPasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void getPasswordFailure(
        String displayCase,
        String username,
        String masterPassword,
        String website,
        String actualUserName,
        String actualMasterPassword,
        String actualWebsite) {
      String expectedPassword = "";
      initDB(username, randomEmail, masterPassword, website, randomLoginName, randomPassword);
      String actualPassword =
          interfacePassword.getPassword(
              actualUserName, actualMasterPassword, actualWebsite, keyAsHex);
      assertEquals(expectedPassword, actualPassword);
    }
  }

  class PasswordComparator implements java.util.Comparator<Password> {
    @Override
    public int compare(Password password1, Password password2) {
      return password1.websiteName.compareTo(password2.websiteName);
    }
  }
}
