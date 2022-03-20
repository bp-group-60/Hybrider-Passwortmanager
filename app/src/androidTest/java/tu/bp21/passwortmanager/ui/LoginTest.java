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
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import static tu.bp21.passwortmanager.StringFunction.*;

public class LoginTest {
  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  static MainActivity mainActivity;
  static ApplicationDatabase database;
  static String randomUsername;
  static String randomEmail;
  static String randomPassword;

  @BeforeEach
  void setUp() throws Exception {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();

      randomUsername = generateRandomString(20);
      randomEmail =
          generateRandomString(20) + "@" + generateRandomString(5) + "." + generateRandomString(5);
      do {
        randomPassword = generateRandomString(20);
      } while (randomPassword.length() < 8);

      onWebView().forceJavascriptEnabled();
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
          .perform(webKeys(randomPassword));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-password-confirm"))
          .perform(webKeys(randomPassword));
      onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
      onWebView().check(webContent(containingTextInBody("Anmeldung")));
      onWebView().check(webContent(containingTextInBody("Benutzerkonto erfolgreich erstellt.")));
    }
  }

  @Test
  void loginSuccessTest() throws Exception {
    onWebView()
        .withElement(findElement(Locator.ID, "input-username"))
        .perform(webKeys(randomUsername));
    onWebView()
        .withElement(findElement(Locator.ID, "input-userPassword"))
        .perform(webKeys(randomPassword));
    onWebView().withElement(findElement(Locator.ID, "login")).perform(webClick());
    onWebView().check(webContent(containingTextInBody("Webseite / Nutzername")));
  }

  @Test
  void loginFailureTest() {
    String differentPassword;
    do {
      differentPassword = generateRandomString(20);
    } while (differentPassword == randomPassword);
    onWebView()
        .withElement(findElement(Locator.ID, "input-username"))
        .perform(webKeys(randomUsername));
    onWebView()
        .withElement(findElement(Locator.ID, "input-userPassword"))
        .perform(webKeys(differentPassword));
    onWebView().withElement(findElement(Locator.ID, "login")).perform(webClick());
    onWebView().check(webContent(containingTextInBody("Anmeldung")));
    onWebView().check(webContent(containingTextInBody("Benutzername oder falsches Passwort")));
  }

  @AfterAll
  static void tearDown() throws Exception {
    mainActivity.deleteDatabase("testDatabase");
  }
}