package tu.bp21.passwortmanager.js_interfaces.interfaces;

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
import tu.bp21.passwortmanager.Constants;
import tu.bp21.passwortmanager.cryptography.Crypto;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import tu.bp21.passwortmanager.db.entities.Website;
import tu.bp21.passwortmanager.db.entities.Url;
import tu.bp21.passwortmanager.db.entities.User;
import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.UserDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.UrlDataAccessObject;

class InterfaceUserTests {
  static MainActivity mainActivity;
  static ApplicationDatabase database;

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  InterfaceUser interfaceUser;
  UserDataAccessObject userDataAccessObject;
  String randomEmail;
  static final int stringMaxLength = 20;
  static final int saltLength = Constants.SALT_LENGTH;

  @AfterAll
  static void tearDown() {
    mainActivity.deleteDatabase("testDatabase");
  }

  @BeforeEach
  void setUp() {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();
    }
    userDataAccessObject = database.getUserDataAccessObject();

    interfaceUser = new InterfaceUser(userDataAccessObject);
    randomEmail = generateRandomString(stringMaxLength) + "@email.de";
  }

  @AfterEach
  void clearDatabase() {
    // Clear Dummy-Data
    database.clearAllTables();
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/InterfaceUserTest/testUser.csv", numLinesToSkip = 1)
  void testExistUser(String userToAdd, String userPasswordToAdd, String userNotExist) {
    userDataAccessObject.addUser(new User(userToAdd, randomEmail, userPasswordToAdd.getBytes()));
    assertFalse(interfaceUser.existUser(userNotExist));
    assertTrue(interfaceUser.existUser(userToAdd));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/InterfaceUserTest/testCheckUser.csv", numLinesToSkip = 1)
  void testCheckUser(
      String displayCase,
      String userToAdd,
      String userPasswordToAdd,
      String userToCheck,
      String userPasswordToCheck) {
    byte[] rightPassword = userPasswordToAdd.getBytes();
    byte[] wrongPassword = userPasswordToCheck.getBytes();
    userDataAccessObject.addUser(new User(userToAdd, randomEmail, rightPassword));
    assertFalse(interfaceUser.checkUser(userToCheck, BaseEncoding.base16().encode(wrongPassword)));
    assertTrue(interfaceUser.checkUser(userToAdd, BaseEncoding.base16().encode(rightPassword)));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "/InterfaceUserTest/testUser.csv", numLinesToSkip = 1)
  void testCreateUser(String userToCreate, String userPasswordToCreate) {
    assertTrue(interfaceUser.createUser(userToCreate, randomEmail, userPasswordToCreate));
    byte[] salt =
        Arrays.copyOf(userDataAccessObject.getUser(userToCreate).hashedUserPassword, saltLength);
    byte[] encryptedPassword = Crypto.computeHash(userPasswordToCreate, salt);
    assertTrue(userDataAccessObject.getUser(userToCreate) != null);
    assertEquals(userToCreate, userDataAccessObject.getUser(userToCreate).username);
    assertEquals(randomEmail, userDataAccessObject.getUser(userToCreate).email);
    assertArrayEquals(
        encryptedPassword, userDataAccessObject.getUser(userToCreate).hashedUserPassword);
    assertFalse(interfaceUser.createUser(userToCreate, randomEmail, userPasswordToCreate));
  }

  @Nested
  @DisplayName("Tests for deleteUser")
  class DeleteUserTest {

    @Test
    @DisplayName("Case: Success")
    void deleteUserSuccess() {
      String username = generateRandomString(stringMaxLength),
          userPassword = generateRandomString(stringMaxLength),
          loginName = generateRandomString(stringMaxLength),
          websiteName1 = generateRandomString(stringMaxLength),
          plainLoginPassword = generateRandomString(stringMaxLength),
          webAddress = generateRandomString(stringMaxLength) + ".com",
          websiteName2 = generateRandomString(stringMaxLength);
      WebsiteDataAccessObject websiteDataAccessObject = database.getPasswordDataAccessObject();
      UrlDataAccessObject urlDataAccessObject = database.getWebsiteDataAccessObject();
      userDataAccessObject.addUser(new User(username, randomEmail, userPassword.getBytes()));
      websiteDataAccessObject.addWebsite(
          new Website(username, websiteName1, loginName, plainLoginPassword.getBytes()));
      websiteDataAccessObject.addWebsite(
          new Website(username, websiteName2, loginName, plainLoginPassword.getBytes()));
      urlDataAccessObject.addUrl(new Url(username, websiteName1, webAddress));

      assertTrue(
          interfaceUser.deleteUser(
              username, BaseEncoding.base16().encode(userPassword.getBytes())));
      assertNull(userDataAccessObject.getUser(username));
      assertNull(websiteDataAccessObject.getWebsite(username, websiteName1));
      assertNull(websiteDataAccessObject.getWebsite(username, websiteName2));
      assertTrue(urlDataAccessObject.getUrlList(username, websiteName1).isEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceUserTest/testCheckUser.csv", numLinesToSkip = 1)
    @DisplayName("Case: Failure")
    void deleteUserFailure(
        String displayCase,
        String username,
        String plainUserPassword,
        String differentUsername,
        String differentPassword) {
      userDataAccessObject.addUser(new User(username, randomEmail, plainUserPassword.getBytes()));

      assertFalse(
          interfaceUser.deleteUser(
              differentUsername, BaseEncoding.base16().encode(differentPassword.getBytes())));
      assertNotNull(userDataAccessObject.getUser(username));
      assertArrayEquals(
          plainUserPassword.getBytes(), userDataAccessObject.getUser(username).hashedUserPassword);
    }
  }
}
