package tu.bp21.passwortmanager.js_interfaces;


import static org.junit.jupiter.api.Assertions.*;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
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

class InterfacePasswordTests {
    InterfacePassword interfacePassword;
    UserDao userDao;
    PasswordDao passwordDao;
    ApplicationDatabase database;
    MainActivity mainActivity;
    static final String email = "randomEmail@blabla.de";
    static final String loginName = "random";
    static final String masterpassword = "randompassword";

    @RegisterExtension
    final ActivityScenarioExtension<MainActivity> scenarioExtension = ActivityScenarioExtension.launch(MainActivity.class);

    @BeforeEach
    void setUp() throws Exception {
        ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();

        scenario.onActivity(activity -> mainActivity = activity);

        database = Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
        userDao = database.getUserDao();
        passwordDao = database.getPasswordDao();

        interfacePassword = new InterfacePassword(passwordDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        database.clearAllTables();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testCreatePassword.csv", numLinesToSkip = 1)
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

    @Test
    void updatePassword() {
    }

    @Test
    void deletePassword() {
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testGetPasswordList.csv", numLinesToSkip = 1)
    void testGetPasswordList(String userToAdd, String userNotExist, String length) throws Exception{

        userDao.addUser(new User(userToAdd, email, masterpassword.getBytes()));
        ArrayList<Password> list = new ArrayList<>();
        addRandomPassword(Integer.parseInt(length), userToAdd, list);
        assertEquals("{\"dataArray\":" + list.toString() + "}", interfacePassword.getPasswordList(userToAdd, masterpassword));
        assertEquals("{\"dataArray\":[]}", interfacePassword.getPasswordList(userNotExist, masterpassword));
    }

    @Test
    void getLoginName() {
    }

    @Test
    void getPassword() {
    }

    void addRandomPassword(int length, String user, ArrayList<Password> list){
        String website, password, loginName;
        for (int i = 0; i < length; i++) {
            website = createRandomString()+".com";
            password = createRandomString();
            loginName = createRandomString();
            Password toAdd = new Password(user, website, loginName,password.getBytes());
            list.add(toAdd);
            passwordDao.addPassword(toAdd);
        }
        list.sort(new PasswordComparator());
    }

    String createRandomString() {
        Random random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = random.nextInt(20);

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
}