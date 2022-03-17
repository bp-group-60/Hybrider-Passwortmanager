package tu.bp21.passwortmanager.ui;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.web.webdriver.Locator;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webKeys;
import static androidx.test.espresso.web.assertion.WebViewAssertions.*;
import static androidx.test.espresso.web.matcher.DomMatchers.containingTextInBody;

import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import static tu.bp21.passwortmanager.StringFunction.*;

public class AddPasswordTest {
  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  static MainActivity mainActivity;
  static ApplicationDatabase database;
  static String randomUsername;
  static String randomEmail;
  static String randomMasterPassword;
  String randomWebsiteName;
  String randomLoginName;
  String randomLoginPassword;
  static final int stringLength = 20;
  static final int smallStringLength = 5;
  static final int masterPasswordMinLength = 8;

  @BeforeEach
  void setUp() throws Exception {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();

      randomUsername = generateRandomString(stringLength);
      randomEmail =
          generateRandomString(stringLength)
              + "@"
              + generateRandomString(smallStringLength)
              + "."
              + generateRandomString(smallStringLength);
      do {
        randomMasterPassword = generateRandomString(stringLength);
      } while (randomMasterPassword.length() < masterPasswordMinLength);

      onWebView().forceJavascriptEnabled();

      // create user for the test
      onWebView()
          .withElement(findElement(Locator.LINK_TEXT, "Konto erstellen"))
          .perform(webClick());
      onWebView()
          .withElement(findElement(Locator.ID, "signup-username"))
          .perform(webKeys(randomUsername));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-email"))
          .perform(webKeys(randomEmail));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-password"))
          .perform(webKeys(randomMasterPassword));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-password-confirm"))
          .perform(webKeys(randomMasterPassword));
      onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
      onWebView().check(webContent(containingTextInBody("Anmeldung")));
      onWebView().check(webContent(containingTextInBody("Benutzerkonto erfolgreich erstellt.")));
    }
    // login
    onWebView()
        .withElement(findElement(Locator.ID, "input-username"))
        .perform(webKeys(randomUsername));
    onWebView()
        .withElement(findElement(Locator.ID, "input-userPassword"))
        .perform(webKeys(randomMasterPassword));
    onWebView().withElement(findElement(Locator.ID, "login")).perform(webClick());
    onWebView().check(webContent(containingTextInBody("Webseite / Nutzername")));

    randomWebsiteName = generateRandomString(stringLength);
    randomLoginName = generateRandomString(stringLength);
    randomLoginPassword = generateRandomString(stringLength);
  }

  @Test
  void addPasswordSuccess() throws Exception {
    onWebView().withElement(findElement(Locator.ID, "add-button")).perform(webClick());
    // onWebView().check(webContent(containingTextInBody("Neues Passwort")));
    Thread.sleep(3000);
    onWebView()
        .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Webseite]"))
        .perform(webKeys(randomWebsiteName));
    onWebView()
        .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
        .perform(webKeys(randomLoginName));
    onWebView()
        .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
        .perform(webKeys(randomLoginPassword));
    onWebView().withElement(findElement(Locator.ID, "commit-button")).perform(webClick());
    onWebView().check(webContent(containingTextInBody(randomLoginName)));
  }

  @AfterAll
  static void tearDown() throws Exception {
    mainActivity.deleteDatabase("testDatabase");
  }
}
