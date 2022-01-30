package tu.bp21.passwortmanager.js_interfaces;

import static org.junit.Assert.*;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Arrays;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.ApplicationDatabase;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.dao.UserDao;

class InterfaceUserTests {
    InterfaceUser interfaceUser;
    UserDao userDao;
    MainActivity mainActivity;
    ApplicationDatabase database;
    @RegisterExtension
    final ActivityScenarioExtension<MainActivity> scenarioExtension = ActivityScenarioExtension.launch(MainActivity.class);
    static final String email = "randomEmail@blabla.de";

    @BeforeEach
    void setUp() throws Exception {

        ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();

        scenario.onActivity(activity -> mainActivity = activity);

        database = Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
        userDao = database.getUserDao();

        interfaceUser = new InterfaceUser(userDao);

    }

    @AfterEach
    void tearDown() throws Exception {
        //Clear Dummy-Data
        database.clearAllTables();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testUser.csv", numLinesToSkip = 1)
    void testExistUser(String userToAdd, String passwordToAdd, String userNotExist) throws Exception {
        userDao.addUser(new User(userToAdd, email, passwordToAdd.getBytes()));
        assertFalse(interfaceUser.existUser(userNotExist));
        assertTrue(interfaceUser.existUser(userToAdd));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testCheckUser.csv", numLinesToSkip = 1)
    void testCheckUser(String userToAdd, String passwordToAdd, String userToCheck, String passwordToCheck) {
        Crypto.setSalt(Crypto.generateSalt(16));
        byte[] encryptedPassword = Crypto.computeHash(passwordToAdd);
        userDao.addUser(new User(userToAdd, email, encryptedPassword));
        assertFalse(interfaceUser.checkUser(userToCheck,passwordToCheck));
        assertTrue( interfaceUser.checkUser(userToAdd,passwordToAdd));
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/testUser.csv", numLinesToSkip = 1)
    void testCreateUser(String userToCreate, String passwordToCreate) {
        assertTrue(interfaceUser.createUser(userToCreate, email, passwordToCreate));
        Crypto.setSalt(Arrays.copyOf(userDao.getUser(userToCreate).password, 16));
        byte[] encryptedPassword = Crypto.computeHash(passwordToCreate);
        assertTrue(userDao.getUser(userToCreate) != null);
        assertEquals(userToCreate, userDao.getUser(userToCreate).username);
        assertEquals(email, userDao.getUser(userToCreate).email);
        assertTrue(Arrays.equals(encryptedPassword, userDao.getUser(userToCreate).password));
        assertFalse(interfaceUser.createUser(userToCreate, email, passwordToCreate));
    }

    @Test
    void deleteUser() {
    }
}