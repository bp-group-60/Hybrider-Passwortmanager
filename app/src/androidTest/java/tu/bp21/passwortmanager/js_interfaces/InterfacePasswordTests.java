package tu.bp21.passwortmanager.js_interfaces;


import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.RandomString.generateRandomString;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;

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
class InterfacePasswordTests {
    InterfacePassword interfacePassword;
    UserDao userDao;
    PasswordDao passwordDao;
    static ApplicationDatabase database;
    static MainActivity mainActivity;
    String randomUser;
    String randomEmail;
    String randomLoginName;
    String randomMasterPassword;
    String randomWebsite;
    String randomPassword;

    @RegisterExtension
    final ActivityScenarioExtension<MainActivity> scenarioExtension = ActivityScenarioExtension.launch(MainActivity.class);

    @BeforeEach
    void setUp() throws Exception {
        ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();

        scenario.onActivity(activity -> mainActivity = activity);

        database = Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
                .allowMainThreadQueries()
                .build();
        userDao = database.getUserDao();
        passwordDao = database.getPasswordDao();

        interfacePassword = new InterfacePassword(passwordDao);

        randomUser = generateRandomString(21);
        randomEmail = generateRandomString(21) + "@email.de";
        randomLoginName = generateRandomString(21);
        randomMasterPassword = generateRandomString(21);
        randomWebsite = generateRandomString(21) + ".de";
        randomPassword = generateRandomString(21);
    }

    @AfterEach
    void clearDatabase() throws Exception {
        //Clear Dummy-Data
        database.clearAllTables();
    }

    @AfterAll
    static void tearDown() throws Exception{
        mainActivity.deleteDatabase("testDatabase");
    }

    @Nested
    @DisplayName("Test for createPassword")
    class createPasswordTest{

        @Test
        @DisplayName("Case: Success")
        void createPasswordSuccess(){
            userDao.addUser(new User(randomUser, randomEmail, randomMasterPassword.getBytes()));
            Crypto.setSalt(Crypto.generateSalt(16));
            Crypto.setGeneratedKey(randomMasterPassword);
            boolean worked = interfacePassword.createPassword(randomUser, randomWebsite, randomLoginName, randomPassword);
            assertTrue(worked);
            checkExpectedDB(randomUser,randomWebsite,randomLoginName,randomPassword);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/createPasswordExist.csv", numLinesToSkip = 1)
        @DisplayName("Case: Already Exist")
        void createPasswordExist(String loginName1, String password1, String loginName2, String password2){
            initDB(randomUser,randomEmail,randomMasterPassword,randomWebsite,loginName1,password1);
            boolean worked = interfacePassword.createPassword(randomUser, randomWebsite, loginName2, password2);
            assertFalse(worked);
            checkExpectedDB(randomUser,randomWebsite,loginName1,password1);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/createPasswordFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: User not exist")
        void createPasswordFailure(String userExist, String userNotExist){
            userDao.addUser(new User(userExist, randomEmail, randomMasterPassword.getBytes()));
            Crypto.setSalt(Crypto.generateSalt(16));
            Crypto.setGeneratedKey(randomMasterPassword);
            boolean worked = interfacePassword.createPassword(userNotExist, randomWebsite, randomLoginName, randomPassword);
            assertFalse(worked);
            assertNull(passwordDao.getPassword(userNotExist,randomWebsite));
        }
    }

    @Nested
    @DisplayName("Tests for updatePassword")
    class updatePasswordTest{

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/updatePasswordSuccess.csv", numLinesToSkip = 1)
        @DisplayName("Case: Update Success")
        void updatePasswordSuccess(String username, String loginName, String password, String newLoginName, String newPassword) {
            initDB(username, randomEmail, randomMasterPassword, randomWebsite, loginName, password);

            assertTrue(interfacePassword.updatePassword(username,randomWebsite,newLoginName, newPassword));

            checkExpectedDB(username,randomWebsite,newLoginName,newPassword);

        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/updatePasswordFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Update Failure")
        void updatePasswordFailure(String username1, String website1, String loginName1, String password1, String username2, String website2, String loginName2, String password2) {
            initDB(username1, randomEmail, randomMasterPassword, website1, loginName1, password1);

            assertFalse(interfacePassword.updatePassword(username2,website2,loginName2,password2));

            checkExpectedDB(username1,website1,loginName1,password1);
        }
    }

  @Nested
  @DisplayName("Tests for deletePassword")
  class deletePasswordTest {

    @Test
    @DisplayName("Case: Delete Success")
    void deletePasswordSuccess() {
      initDB(randomUser, randomEmail, randomMasterPassword, randomWebsite, randomLoginName, randomPassword);
      String randomUrl = generateRandomString(21);
      WebsiteDao websiteDao = database.getWebsiteDao();
      websiteDao.addWebsite(new Website(randomUser,randomWebsite,randomUrl));

      assertTrue(interfacePassword.deletePassword(randomUser, randomWebsite));
      assertNull(passwordDao.getPassword(randomUser,randomWebsite));
      assertTrue(websiteDao.getWebsiteList(randomUser,randomWebsite).isEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/deletePasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Delete Failure")
    void deletePasswordFailure(
        String username1,
        String website1,
        String username2,
        String website2) {
      initDB(username1, randomEmail, randomMasterPassword, website1, randomLoginName, randomPassword);

      assertFalse(interfacePassword.deletePassword(username2,website2));

      checkExpectedDB(username1, website1, randomLoginName, randomPassword);
    }

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/testGetPasswordList.csv", numLinesToSkip = 1)
    void testGetPasswordList(String usernameToAdd, String differentUsername, int amount) throws Exception{

        userDao.addUser(new User(usernameToAdd, randomEmail, randomMasterPassword.getBytes()));
        ArrayList<Password> list = new ArrayList<>();
        addRandomPassword(amount, usernameToAdd, list);
        assertEquals("{\"dataArray\":" + list + "}", interfacePassword.getPasswordList(usernameToAdd, randomMasterPassword));
        assertEquals("{\"dataArray\":[]}", interfacePassword.getPasswordList(differentUsername, randomMasterPassword));
    }

    @Nested
    @DisplayName("Tests for getLoginName")
    class getLoginNameTest{

        @Test
        @DisplayName("Case: Success")
        void getLoginNameSuccess(){
            String expectedLoginName = randomLoginName;
            initDB(randomUser,randomEmail, randomMasterPassword,randomWebsite, expectedLoginName, randomPassword);
            String actualLoginName = interfacePassword.getLoginName(randomUser, randomMasterPassword, randomWebsite);
            assertTrue(expectedLoginName.equals(actualLoginName));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/getLoginNameFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Failure")
        void getLoginNameFailure(String username, String masterPassword, String website, String actualUserName, String actualMasterPassword, String actualWebsite){
            String expectedLoginName = randomLoginName;
            initDB(username, randomEmail, masterPassword, website, expectedLoginName, randomPassword);
            String actualLoginName = interfacePassword.getLoginName(actualUserName, actualMasterPassword, actualWebsite);
            assertFalse(expectedLoginName.equals(actualLoginName));
        }
    }

    @Nested
    @DisplayName("Tests for getPassword")
    class getPasswordTest{

        @Test
        @DisplayName("Case: Success")
        void getPasswordSuccess(){
            String expectedPassword = randomPassword;
            initDB(randomUser,randomEmail, randomMasterPassword,randomWebsite, randomLoginName, expectedPassword);
            String actualPassword = interfacePassword.getPassword(randomUser, randomMasterPassword, randomWebsite);
            assertTrue(expectedPassword.equals(actualPassword));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/getPasswordFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Failure")
        void getPasswordFailure(String username, String masterPassword, String website, String actualUserName, String actualMasterPassword, String actualWebsite){
            String expectedPassword = randomPassword;
            initDB(randomUser,randomEmail, randomMasterPassword,randomWebsite, randomLoginName, expectedPassword);
            String actualPassword = interfacePassword.getPassword(actualUserName, actualMasterPassword, actualWebsite);
            assertFalse(expectedPassword.equals(actualPassword));
        }
    }

    /**
     * this method adds a random amounts of entity Password into the DB under the given user
     * the added entries are also saved into the ArrayList
     */
    void addRandomPassword(int amount, String user, ArrayList<Password> list){
        String website, password, loginName;
        for (int i = 0; i < amount; i++) {
            website = generateRandomString(21)+".com";
            password = generateRandomString(21);
            loginName = generateRandomString(21);
            Password toAdd = new Password(user, website, loginName,password.getBytes());
            list.add(toAdd);
            passwordDao.addPassword(toAdd);
        }
        list.sort(new PasswordComparator());
    }

    class PasswordComparator implements java.util.Comparator<Password>{
        @Override
        public int compare(Password password1, Password password2) {
            return password1.websiteName.compareTo(password2.websiteName);
        }

    }

    //this method adds an User entity and Password entity into the DB
    void initDB(String username, String email, String masterPassword, String website, String loginName, String password){
        Crypto.setSalt(Crypto.generateSalt(16));
        Crypto.setGeneratedKey(masterPassword);
        userDao.addUser(new User(username, email, masterPassword.getBytes()));
        passwordDao.addPassword(new Password(username,website,loginName,Crypto.encrypt(password)));
    }

    //this method checks if the given loginName and password matches the loginName and password of the given Entity specified by username and website
    void checkExpectedDB(String username, String website, String loginName, String password){
        Password expected = passwordDao.getPassword(username,website);
        assertTrue(expected!=null);
        assertTrue(expected.user.equals(username));
        assertTrue(expected.websiteName.equals(website));
        assertTrue(expected.loginName.equals(loginName));
        assertTrue(Crypto.decrypt(expected.password).equals(password));
    }

}