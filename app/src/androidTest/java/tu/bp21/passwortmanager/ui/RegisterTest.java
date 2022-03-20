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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import static tu.bp21.passwortmanager.StringFunction.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterTest {

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  static MainActivity mainActivity;
  static ApplicationDatabase database;
  static String defaultUsername;
  String randomUsername;
  String randomEmail;
  String randomUserPassword;
  static final int stringMaxLength = 20;
  static final int domainMinLength = 2;
  static final int domainMaxLength = 5;
  static final int usernameMinLength = 3;
  static final int userPasswordMinLength = 8;
  static final int loadDelay = 500;

  @BeforeEach
  void setUp() throws Exception {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();
      defaultUsername = generateRandomString(usernameMinLength, stringMaxLength);
    }
    randomUsername = generateRandomString(usernameMinLength, stringMaxLength);
    randomEmail =
        generateRandomString(stringMaxLength)
            + "@"
            + generateRandomString(domainMinLength)
            + "."
            + generateRandomString(domainMinLength, domainMaxLength);

    randomUserPassword = generateRandomString(userPasswordMinLength, stringMaxLength);

    onWebView().forceJavascriptEnabled();
    onWebView().withElement(findElement(Locator.LINK_TEXT, "Konto erstellen")).perform(webClick());
    onWebView().check(webContent(containingTextInBody("Konto erstellen")));
  }

  @Test
  @Order(1)
  void registerSuccess() throws Exception {
    fillDataForm(defaultUsername, randomEmail, randomUserPassword, randomUserPassword);
    onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
    // should be in login page
    onWebView().check(webContent(containingTextInBody("Anmeldung")));
    onWebView().check(webContent(containingTextInBody("Benutzerkonto erfolgreich erstellt.")));
  }

  @Test
  @Order(2)
  void registerUserExist() {
    fillDataForm(defaultUsername, randomEmail, randomUserPassword, randomUserPassword);

    onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());

    // should stay in register page
    onWebView().check(webContent(containingTextInBody("Konto erstellen")));
    onWebView().check(webContent(containingTextInBody("Benutzername bereits vergeben")));
  }

  @Test
  void registerUsernameFormatNotValid() {
    String username = generateRandomString(usernameMinLength - 1);
    fillDataForm(username, randomEmail, randomUserPassword, randomUserPassword);
    onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
    // should stay in register page
    onWebView().check(webContent(containingTextInBody("Konto erstellen")));
    onWebView()
        .check(webContent(containingTextInBody("Benutzername muss mindestens 3 Zeichen haben")));
  }

  @Nested
  class emailInvalidTest {
    @Test
    void emailNoDomain() throws Exception {
      String email =
          generateRandomString(stringMaxLength) + "@" + generateRandomString(stringMaxLength);
      fillDataForm(randomUsername, email, randomUserPassword, randomUserPassword);
      checkEmailFormatError();
    }

    @Test
    void emailWithShortDomain() throws Exception {
      String email =
          generateRandomString(stringMaxLength)
              + "@"
              + generateRandomString(stringMaxLength)
              + "."
              + generateRandomString(domainMinLength - 1);
      ;
      fillDataForm(randomUsername, email, randomUserPassword, randomUserPassword);
      checkEmailFormatError();
    }

    @Test
    void emailWithLongDomain() throws Exception {
      String email =
          generateRandomString(stringMaxLength)
              + "@"
              + generateRandomString(stringMaxLength)
              + "."
              + generateRandomString(domainMaxLength + 1, domainMaxLength + 1);
      ;
      fillDataForm(randomUsername, email, randomUserPassword, randomUserPassword);
      checkEmailFormatError();
    }

    void checkEmailFormatError() throws Exception {
      onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
      Thread.sleep(loadDelay);
      // should stay in register page
      onWebView().check(webContent(containingTextInBody("Konto erstellen")));
      onWebView().check(webContent(containingTextInBody("Bitte gib eine g")));
      onWebView().check(webContent(containingTextInBody("ltige E-Mail-Adresse ein.")));
    }
  }

  @Test
  void registerUserPasswordFormatNotValid() {
    String userPassword = generateRandomString(userPasswordMinLength - 1);
    fillDataForm(randomUsername, randomEmail, userPassword, userPassword);
    onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
    // should stay in register page
    onWebView().check(webContent(containingTextInBody("Konto erstellen")));
    onWebView()
        .check(webContent(containingTextInBody("Passwort muss mindestens 8 Zeichen enthalten")));
  }

  @Test
  void registerUserPasswordConfirmNotMatch() {
    String userPassword = generateRandomString(userPasswordMinLength - 1);
    fillDataForm(randomUsername, randomEmail, userPassword, randomUserPassword);
    onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
    // should stay in register page
    onWebView().check(webContent(containingTextInBody("Konto erstellen")));
    onWebView().check(webContent(containingTextInBody("stimmen nicht")));
  }

  void fillDataForm(
      String username, String email, String userPassword, String userPasswordConfirm) {
    onWebView().withElement(findElement(Locator.ID, "signup-username")).perform(webKeys(username));
    onWebView().withElement(findElement(Locator.ID, "signup-email")).perform(webKeys(email));
    onWebView()
        .withElement(findElement(Locator.ID, "signup-password"))
        .perform(webKeys(userPassword));
    onWebView()
        .withElement(findElement(Locator.ID, "signup-password-confirm"))
        .perform(webKeys(userPasswordConfirm));
  }

  @AfterAll
  static void tearDown() throws Exception {
    mainActivity.deleteDatabase("testDatabase");
  }
}
