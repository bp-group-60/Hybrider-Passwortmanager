package tu.bp21.passwortmanager.js_interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Arrays;

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

class InterfaceUserTests {
  static MainActivity mainActivity;
  static ApplicationDatabase database;

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  InterfaceUser interfaceUser;
  UserDao userDao;
  String randomEmail;

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

    interfaceUser = new InterfaceUser(userDao);
    randomEmail = generateRandomString(20) + "@email.de";
  }

  @AfterEach
  void clearDatabase() throws Exception {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/User/testUser.csv", numLinesToSkip = 1)
  void testExistUser(String userToAdd, String passwordToAdd, String userNotExist) throws Exception {
    userDao.addUser(new User(userToAdd, randomEmail, passwordToAdd.getBytes()));
    assertFalse(interfaceUser.existUser(userNotExist));
    assertTrue(interfaceUser.existUser(userToAdd));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/User/testCheckUser.csv", numLinesToSkip = 1)
  void testCheckUser(
      String displayCase,
      String userToAdd,
      String passwordToAdd,
      String userToCheck,
      String passwordToCheck) {
    byte[] rightPassword = passwordToAdd.getBytes();
    byte[] wrongPassword = passwordToCheck.getBytes();
    userDao.addUser(new User(userToAdd, randomEmail, rightPassword));
    assertFalse(interfaceUser.checkUser(userToCheck, BaseEncoding.base16().encode(wrongPassword)));
    assertTrue(interfaceUser.checkUser(userToAdd, BaseEncoding.base16().encode(rightPassword)));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/User/testUser.csv", numLinesToSkip = 1)
  void testCreateUser(String userToCreate, String passwordToCreate) {
    assertTrue(interfaceUser.createUser(userToCreate, randomEmail, passwordToCreate));
    byte[] salt = Arrays.copyOf(userDao.getUser(userToCreate).password, 16);
    byte[] encryptedPassword = Crypto.computeHash(passwordToCreate, salt);
    assertTrue(userDao.getUser(userToCreate) != null);
    assertEquals(userToCreate, userDao.getUser(userToCreate).username);
    assertEquals(randomEmail, userDao.getUser(userToCreate).email);
    assertArrayEquals(encryptedPassword, userDao.getUser(userToCreate).password);
    assertFalse(interfaceUser.createUser(userToCreate, randomEmail, passwordToCreate));
  }

  @Nested
  @DisplayName("Tests for deleteUser")
  class deleteUserTest {

    @Test
    @DisplayName("Case: Success")
    void deleteUserSuccess() {
      String username = generateRandomString(20),
          masterPassword = generateRandomString(20),
          loginName = generateRandomString(20),
          website1 = generateRandomString(20) + ".com",
          password = generateRandomString(20),
          url = generateRandomString(20),
          website2 = website1 + ".de";
      PasswordDao passwordDao = database.getPasswordDao();
      WebsiteDao websiteDao = database.getWebsiteDao();
      userDao.addUser(new User(username, randomEmail, masterPassword.getBytes()));
      passwordDao.addPassword(new Password(username, website1, loginName, password.getBytes()));
      passwordDao.addPassword(new Password(username, website2, loginName, password.getBytes()));
      websiteDao.addWebsite(new Website(username, website1, url));

      assertTrue(
          interfaceUser.deleteUser(
              username, BaseEncoding.base16().encode(masterPassword.getBytes())));
      assertNull(userDao.getUser(username));
      assertNull(passwordDao.getPassword(username, website1));
      assertNull(passwordDao.getPassword(username, website2));
      assertTrue(websiteDao.getWebsiteList(username, website1).isEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/User/testCheckUser.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void deleteUserFailure(
        String displayCase,
        String username,
        String password,
        String differentUsername,
        String differentPassword) {
      userDao.addUser(new User(username, randomEmail, password.getBytes()));

      assertFalse(
          interfaceUser.deleteUser(
              differentUsername, BaseEncoding.base16().encode(differentPassword.getBytes())));
      assertNotNull(userDao.getUser(username));
      assertArrayEquals(password.getBytes(), userDao.getUser(username).password);
    }
  }
}
