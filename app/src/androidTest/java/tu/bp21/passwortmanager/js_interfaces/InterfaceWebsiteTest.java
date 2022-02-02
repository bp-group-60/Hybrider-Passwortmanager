package tu.bp21.passwortmanager.js_interfaces;


import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.*;


import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;
import java.util.Random;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.ApplicationDatabase;
import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.Website;
import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.dao.UserDao;
import tu.bp21.passwortmanager.db.dao.WebsiteDao;

class InterfaceWebsiteTest {
    InterfaceWebsite interfaceWebsite;
    UserDao userDao;
    PasswordDao passwordDao;
    WebsiteDao websiteDao;
    static ApplicationDatabase database;
    static MainActivity mainActivity;
    String randomUser;
    String randomEmail;
    String randomLoginName;
    String randomMasterPassword;
    String randomWebsite;
    String randomPassword;
    String randomUrl;

    @RegisterExtension
    final ActivityScenarioExtension<MainActivity> scenarioExtension = ActivityScenarioExtension.launch(MainActivity.class);

    @BeforeEach
    public void setUp() throws Exception {
        ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();

        scenario.onActivity(activity -> mainActivity = activity);

        database = Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
                .allowMainThreadQueries()
                .build();
        userDao = database.getUserDao();
        passwordDao = database.getPasswordDao();
        websiteDao = database.getWebsiteDao();

        interfaceWebsite = new InterfaceWebsite(websiteDao);

        randomUser = generateRandomString(20);
        randomEmail = generateRandomString(20) + "@email.de";
        randomLoginName = generateRandomString(20);
        randomMasterPassword = generateRandomString(20);
        randomWebsite = generateRandomString(20) + ".de";
        randomPassword = generateRandomString(20);
        randomUrl = generateRandomString(5)+"."+generateRandomString(20)+".com";
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

    @Test
    public void saveUrl() {
    }

    @Nested
    @DisplayName("Tests for saveUrl")
    class saveUrlTest{

        @Test
        @DisplayName("Case: Success")
        void saveUrlSuccess(){
            initDB(randomUser,randomEmail,randomMasterPassword,randomWebsite,randomLoginName,randomPassword);
            assertTrue(interfaceWebsite.saveUrl(randomUser,randomWebsite,randomUrl));
            checkExpectedDB(randomUser,randomWebsite,randomUrl);
        }

        @Test
        @DisplayName("Case: Already Exist")
        void saveUrlExisted(){
            initDB(randomUser,randomEmail,randomMasterPassword,randomWebsite,randomLoginName,randomPassword);
            websiteDao.addWebsite(new Website(randomUser,randomWebsite,randomUrl));
            assertFalse(interfaceWebsite.saveUrl(randomUser,randomWebsite,randomUrl));
            checkExpectedDB(randomUser,randomWebsite,randomUrl);
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Website/saveUrlFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Failure")
        void saveUrlFailure(String userExistedInDB, String websiteExistedInDB, String userGiven, String websiteGiven, String urlGiven){
            urlGiven = convertNullToEmptyString(urlGiven);
            initDB(userExistedInDB,randomEmail,randomMasterPassword,websiteExistedInDB,randomLoginName,randomPassword);
            assertFalse(interfaceWebsite.saveUrl(userGiven,websiteGiven,urlGiven));
            assertNull(getWebsite(userGiven,websiteGiven,urlGiven));
        }
    }

    @Nested
    @DisplayName("Tests for deleteUrl")
    class deleteUrlTest{

        @Test
        @DisplayName("Case: Success")
        void deleteUrlSuccess(){
            initDB(randomUser,randomEmail,randomMasterPassword,randomWebsite,randomLoginName,randomPassword);
            websiteDao.addWebsite(new Website(randomUser,randomWebsite,randomUrl));
            assertTrue(interfaceWebsite.deleteUrl(randomUser,randomWebsite,randomUrl));
            assertNull(getWebsite(randomUser,randomWebsite,randomUrl));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Website/deleteUrlFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Failure")
        void deleteUrlFailure(String usernameExistedInDB, String websiteExistedInDB, String urlExistedInDB, String usernameGiven, String websiteGiven, String urlGiven){
            initDB(usernameExistedInDB,randomEmail,randomMasterPassword,websiteExistedInDB,randomLoginName,randomPassword);
            websiteDao.addWebsite(new Website(usernameExistedInDB,websiteExistedInDB,urlExistedInDB));
            assertFalse(interfaceWebsite.deleteUrl(usernameGiven,websiteGiven,urlGiven));
            checkExpectedDB(usernameExistedInDB,websiteExistedInDB,urlExistedInDB);
        }
    }

    @Nested
    @DisplayName("Test for getUrlList")
    class getUrlListTest {

        @Test
        @DisplayName("Case: Success")
        void getUrlListSuccess(){
            ArrayList<Website> list = new ArrayList<>();
            initDB(randomUser,randomEmail,randomMasterPassword,randomWebsite,randomLoginName,randomPassword);
            addRandomWebsiteUrl(randomUser,randomWebsite, list);
            assertEquals("{\"dataArray\":" + list + "}", interfaceWebsite.getUrlList(randomUser, randomWebsite));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/Website/getUrlListFailure.csv", numLinesToSkip = 1)
        @DisplayName("Case: Failure")
        void getUrlListFailure(String usernameExistedInDB, String websiteExistedInDB, String usernameGiven, String websiteGiven){
            ArrayList<Website> list = new ArrayList<>();
            initDB(usernameExistedInDB,randomEmail,randomMasterPassword,websiteExistedInDB,randomLoginName,randomPassword);
            addRandomWebsiteUrl(usernameExistedInDB,websiteExistedInDB, list);
            assertNotEquals("{\"dataArray\":" + list + "}", interfaceWebsite.getUrlList(usernameGiven, websiteGiven));
            assertEquals("{\"dataArray\":[]}", interfaceWebsite.getUrlList(usernameGiven, websiteGiven));
        }
    }

  /** this method adds an User entity and Password entity into the DB */
  void initDB(
      String username,
      String email,
      String masterPassword,
      String website,
      String loginName,
      String password) {
        userDao.addUser(new User(username, email, masterPassword.getBytes()));
        passwordDao.addPassword(new Password(username,website,loginName,password.getBytes()));
    }

  /**
   * this method checks if the given Url matches the Url of the given Website Entity specified by
   * username and website
   */
  void checkExpectedDB(String username, String website, String url) {
        Website expected = getWebsite(username,website,url);

        assertTrue(expected!=null);
        assertTrue(expected.user.equals(username));
        assertTrue(expected.website.equals(website));
        assertTrue(expected.url.equals(url));
    }

    /**
     * this method finds the saved Website entity given the username, website and Url
     * @return
     */
    Website getWebsite(String username, String website, String url){
        for (Website web : websiteDao.getWebsiteList(username,website)) {
            if(web.url.equals(url))
                return web;
        }
        return null;
    }

    /**
     * this method adds a random amount (less than 20) of entity Website into the DB under the given username and website
     * the added entries are also saved into the ArrayList
     */
    void addRandomWebsiteUrl(String username, String website, ArrayList<Website> list){
        String url;
        int amount = new Random().nextInt(20) + 1;
        for (int i = 0; i < amount; i++) {
            url = generateRandomString(20)+".com";
            Website toAdd = new Website(username,website,url);
            list.add(toAdd);
            websiteDao.addWebsite(toAdd);
        }
        list.sort(new WebsiteComparator());
    }

    class WebsiteComparator implements java.util.Comparator<Website>{
        @Override
        public int compare(Website website1, Website website2) {
            return website1.url.compareTo(website2.url);
        }

    }
}