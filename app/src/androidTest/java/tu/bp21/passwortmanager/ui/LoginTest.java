package tu.bp21.passwortmanager.ui;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.web.webdriver.Locator;

import static androidx.test.espresso.web.matcher.DomMatchers.hasElementWithId;
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

class LoginTest {
  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  static MainActivity mainActivity;
  static ApplicationDatabase database;
  static String randomUsername;
  static String randomEmail;
  static String randomUserPassword;
  static final int stringMaxLength = 20;
  static final int domainMinLength = 2;
  static final int domainMaxLength = 5;
  static final int usernameMinLength = 3;
  static final int userPasswordMinLength = 8;

  @BeforeEach
  void setUp() {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();

      randomUsername = generateRandomString(usernameMinLength, stringMaxLength);
      randomEmail =
          generateRandomString(stringMaxLength)
              + "@"
              + generateRandomString(domainMaxLength)
              + "."
              + generateRandomString(domainMinLength, domainMaxLength);

      randomUserPassword = generateRandomString(userPasswordMinLength, stringMaxLength);

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
          .perform(webKeys(randomUserPassword));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-password-confirm"))
          .perform(webKeys(randomUserPassword));
      onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
      onWebView().check(webContent(containingTextInBody("Anmeldung")));
      onWebView().check(webContent(containingTextInBody("Benutzerkonto erfolgreich erstellt.")));
    }
  }

  @Test
  void loginSuccessTest() {
    onWebView()
        .withElement(findElement(Locator.ID, "input-username"))
        .perform(webKeys(randomUsername));
    onWebView()
        .withElement(findElement(Locator.ID, "input-userPassword"))
        .perform(webKeys(randomUserPassword));
    onWebView().withElement(findElement(Locator.ID, "login")).perform(webClick());
    onWebView().check(webContent(hasElementWithId("add-button")));
  }

  @Test
  void loginFailureTest() {
    String differentPassword;
    do {
      differentPassword = generateRandomString(stringMaxLength);
    } while (differentPassword.equals(randomUserPassword));
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
  static void tearDown() {
    mainActivity.deleteDatabase("testDatabase");
  }
}
