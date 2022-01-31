package tu.bp21.passwortmanager.js_interfaces;


import static org.junit.jupiter.api.Assertions.*;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.R;
import tu.bp21.passwortmanager.db.ApplicationDatabase;
import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.dao.UserDao;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName(value = "Tests for InterfacePassword")
class InterfacePasswordTests {
    InterfacePassword interfacePassword;
    UserDao userDao;
    PasswordDao passwordDao;
    static ApplicationDatabase database;
    static MainActivity mainActivity;
    static final String email = "randomEmail@blabla.de";
    static final String loginName = "random";
    static final String masterpassword = "randompassword";

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

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/testCreatePassword.csv", numLinesToSkip = 1)
    void testCreatePassword(String userToAdd, String websiteToAdd, String passwordTooAdd, String userNotExist
                            ) throws Exception{
        userDao.addUser(new User(userToAdd, email, masterpassword.getBytes()));
        Crypto.setSalt(Crypto.generateSalt(16));
        Crypto.setGeneratedKey(masterpassword);
        boolean worked = interfacePassword.createPassword(userToAdd, websiteToAdd, loginName, passwordTooAdd);
        assertTrue(worked);
        boolean alreadyExist = !interfacePassword.createPassword(userToAdd, websiteToAdd, loginName, passwordTooAdd);
        assertTrue(alreadyExist);

        boolean workedNot = interfacePassword.createPassword(userNotExist, websiteToAdd, loginName, passwordTooAdd);
        assertFalse(workedNot);
    }

    @Nested
    @DisplayName("Tests for updatePassword")
    class updatePasswordTest{

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/updatePasswordSuccess.csv", numLinesToSkip = 1)
        @DisplayName("Case: Update Success")
        void updatePasswordSuccess(String username, String loginName, String password, String newLoginName, String newPassword) {
            String website = createRandomString()+".com";
            initDB(username,email,website,loginName,password);

            assertTrue(interfacePassword.updatePassword(username,website,newLoginName, newPassword));

            checkExpectedDB(username,website,newLoginName,newPassword);

        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Password/updatePasswordFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Update Failure")
        void updatePasswordFailure(String username1, String website1, String loginName1, String password1, String username2, String website2, String loginName2, String password2) {
            initDB(username1,email,website1,loginName1,password1);

            assertFalse(interfacePassword.updatePassword(username2,website2,loginName2,password2));

            checkExpectedDB(username1,website1,loginName1,password1);
        }
    }

    @Test
    void deletePassword() {
    }

  @Nested
  @DisplayName("Tests for deletePassword")
  class deletePasswordTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/deletePasswordSuccess.csv", numLinesToSkip = 1)
    @DisplayName("Case: Delete Success")
    void deletePasswordSuccess(
        String username,
        String website) {
        String loginName = createRandomString();
        String password = createRandomString();
      initDB(username, email, website, loginName, password);

      assertTrue(interfacePassword.deletePassword(username, website));
      assertTrue(passwordDao.getPassword(username,website) == null);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/deletePasswordFailure.csv", numLinesToSkip = 1)
    @DisplayName("Case: Delete Failure")
    void deletePasswordFailure(
        String username1,
        String website1,
        String username2,
        String website2) {
        String loginName = createRandomString();
        String password = createRandomString();
      initDB(username1, email, website1, loginName, password);

      assertFalse(interfacePassword.deletePassword(username2,website2));

      checkExpectedDB(username1, website1, loginName, password);
    }

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Password/testGetPasswordList.csv", numLinesToSkip = 1)
    void testGetPasswordList(String userToAdd, String userNotExist, int amount) throws Exception{

        userDao.addUser(new User(userToAdd, email, masterpassword.getBytes()));
        ArrayList<Password> list = new ArrayList<>();
        addRandomPassword(amount, userToAdd, list);
        assertEquals("{\"dataArray\":" + list + "}", interfacePassword.getPasswordList(userToAdd, masterpassword));
        assertEquals("{\"dataArray\":[]}", interfacePassword.getPasswordList(userNotExist, masterpassword));
    }

    @Test
    void getLoginName() {

    }

    @Test
    void getPassword() {
    }

    /**
     * this method adds a random amounts of entity Password into the DB under the given user
     * the added entries are also saved into the ArrayList
     */
    void addRandomPassword(int amount, String user, ArrayList<Password> list){
        String website, password, loginName;
        for (int i = 0; i < amount; i++) {
            website = createRandomString()+".com";
            password = createRandomString();
            loginName = createRandomString();
            Password toAdd = new Password(user, website, loginName,password.getBytes());
            list.add(toAdd);
            passwordDao.addPassword(toAdd);
        }
        list.sort(new PasswordComparator());
    }

    //this method generate a random numberalphabetic String with not more than 20 characters
    String createRandomString() {
        Random random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = random.nextInt(21);

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    class PasswordComparator implements java.util.Comparator<Password>{
        @Override
        public int compare(Password password1, Password password2) {
            return password1.websiteName.compareTo(password2.websiteName);
        }

    }

    //this method adds an User entity and Password entity into the DB
    void initDB(String username, String email, String website, String loginName, String password){
        Crypto.setSalt(Crypto.generateSalt(16));
        Crypto.setGeneratedKey(masterpassword);
        userDao.addUser(new User(username, email, masterpassword.getBytes()));
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