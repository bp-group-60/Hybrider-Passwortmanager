package tu.bp21.passwortmanager.ui;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.web.webdriver.Locator;

import static androidx.test.espresso.web.matcher.DomMatchers.hasElementWithId;
import static androidx.test.espresso.web.matcher.DomMatchers.hasElementWithXpath;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webKeys;
import static androidx.test.espresso.web.assertion.WebViewAssertions.*;
import static androidx.test.espresso.web.matcher.DomMatchers.containingTextInBody;

import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import static tu.bp21.passwortmanager.StringFunction.*;
import static tu.bp21.passwortmanager.WebViewFunction.getWebViewInstance;
import static tu.bp21.passwortmanager.ui.DomMatchersExtended.hasNoElementWithXpath;

import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.WebView;
import static tu.bp21.passwortmanager.ui.UITestConstaints.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class AppHTMLTest {
  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  ActivityScenario<MainActivity> scenario;
  static MainActivity mainActivity;
  static ApplicationDatabase database;
  static String defaultUsername;
  static String defaultEmail;
  static String defaultUserPassword;
  static String defaultWebsiteName;
  static String defaultLoginName;
  static String defaultLoginPassword;
  static String defaultWebAddress1;
  static String defaultWebAddress2;

  @BeforeEach
  void setUp() throws Exception {
    if (mainActivity == null) {
      scenarioExtension.getScenario().onActivity(activity -> mainActivity = activity);

      database =
          Room.databaseBuilder(mainActivity, ApplicationDatabase.class, "testDatabase")
              .allowMainThreadQueries()
              .build();

      defaultUsername = generateRandomString(usernameMinLength, stringMaxLength);
      defaultEmail =
          generateRandomString(stringMaxLength)
              + "@"
              + generateRandomString(domainMaxLength)
              + "."
              + generateRandomString(domainMinLength, domainMaxLength);

      defaultUserPassword = generateRandomString(userPasswordMinLength, stringMaxLength);

      defaultWebsiteName = generateRandomString(stringMaxLength);
      defaultLoginName = generateRandomString(stringMaxLength);
      defaultLoginPassword = generateRandomString(stringMaxLength);
      defaultWebAddress1 = generateRandomString(stringMaxLength) + ".com";
      defaultWebAddress2 = generateRandomString(stringMaxLength) + ".de";

      onWebView().forceJavascriptEnabled();

      // create user for the test
      onWebView()
          .withElement(findElement(Locator.LINK_TEXT, "Konto erstellen"))
          .perform(webClick());
      onWebView()
          .withElement(findElement(Locator.ID, "signup-username"))
          .perform(webKeys(defaultUsername));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-email"))
          .perform(webKeys(defaultEmail));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-password"))
          .perform(webKeys(defaultUserPassword));
      onWebView()
          .withElement(findElement(Locator.ID, "signup-password-confirm"))
          .perform(webKeys(defaultUserPassword));
      onWebView().withElement(findElement(Locator.ID, "submit-button")).perform(webClick());
      onWebView().check(webContent(containingTextInBody("Anmeldung")));
      onWebView().check(webContent(containingTextInBody("Benutzerkonto erfolgreich erstellt.")));
    }

    scenario = scenarioExtension.getScenario();
    // login
    onWebView()
        .withElement(findElement(Locator.ID, "input-username"))
        .perform(webKeys(defaultUsername));
    onWebView()
        .withElement(findElement(Locator.ID, "input-userPassword"))
        .perform(webKeys(defaultUserPassword));
    onWebView().withElement(findElement(Locator.ID, "login")).perform(webClick());
    Thread.sleep(loadDelay);
    onWebView().check(webContent(hasElementWithId("add-button")));
  }

  @Test
  void logoutTest() throws Exception {
    onWebView().withElement(findElement(Locator.ID, "more-button")).perform(webClick());
    onWebView().withElement(findElement(Locator.ID, "logout")).perform(webClick());
    // check return back to login page
    onWebView().check(webContent(containingTextInBody("Anmeldung")));
    onWebView().check(webContent(hasElementWithId("login")));
  }

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class AddWebsiteTest {

    @BeforeEach
    void startNewWebsitePage() throws Exception {
      // go to add password page
      onWebView().withElement(findElement(Locator.ID, "add-button")).perform(webClick());
      // wait for page to load
      Thread.sleep(loadDelay);

      onWebView().check(webContent(containingTextInBody("Neues Passwort")));
    }

    @Test
    @Order(1)
    void addWebsiteSuccess() throws Exception {
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Webseite]"))
          .perform(webKeys(defaultWebsiteName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
          .perform(webKeys(defaultLoginName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(webKeys(defaultLoginPassword));
      onWebView().withElement(findElement(Locator.ID, "commit-button")).perform(webClick());

      // toast should be displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
      onWebView().check(webContent(containingTextInBody("Passwort hinzu")));

      // wait for onsenui element to be generated
      Thread.sleep(loadDelay);

      // return back to overview after adding password
      onWebView().check(webContent(hasElementWithId("add-button")));
      // check element exist
      onWebView()
          .check(webContent(containingTextInBody(defaultWebsiteName + " / " + defaultLoginName)));

      // go to the saved website to check loginName and loginPassword
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      checkLoginNamePasswordTextField(defaultLoginName, defaultLoginPassword);
    }

    @Test
    void addEmptyWebsite() {
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
          .perform(webKeys(defaultLoginName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(webKeys(defaultLoginPassword));
      onWebView().withElement(findElement(Locator.ID, "commit-button")).perform(webClick());

      // toast should be displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
      onWebView().check(webContent(containingTextInBody("Fehler beim speichern!")));
      // stay on add website page
      onWebView().check(webContent(containingTextInBody("Neues Passwort")));
    }

    @Test
    @Order(2)
    void addExistedWebsite() {
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Webseite]"))
          .perform(webKeys(defaultWebsiteName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
          .perform(webKeys(defaultLoginName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(webKeys(defaultLoginPassword));
      onWebView().withElement(findElement(Locator.ID, "commit-button")).perform(webClick());

      // toast should be displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
      onWebView().check(webContent(containingTextInBody("Fehler beim speichern!")));
      // stay on add website page
      onWebView().check(webContent(containingTextInBody("Neues Passwort")));
    }
  }

  @Nested
  @Order(3)
  class ShowPasswordButtonTest {

    @Test
    void inAddWebsitePage() throws Exception {
      // go to add website page
      onWebView().withElement(findElement(Locator.ID, "add-button")).perform(webClick());
      // wait for page to load
      Thread.sleep(loadDelay);

      onWebView().check(webContent(containingTextInBody("Neues Passwort")));
      showPasswordTest();
    }

    @Test
    void inWebsiteDetailPage() throws Exception {
      // go to website detail page
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      // wait for page to load
      Thread.sleep(loadDelay);

      onWebView().check(webContent(hasElementWithId("edit-button")));
      showPasswordTest();
    }

    @Test
    void inWebsiteEditPage() throws Exception {
      // go to website edit page
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      onWebView().withElement(findElement(Locator.ID, "edit-button")).perform(webClick());
      Thread.sleep(loadDelay);

      onWebView().check(webContent(hasElementWithId("save-button")));
      showPasswordTest();
    }

    void showPasswordTest() {
      onWebView()
          .check(
              webContent(
                  hasElementWithXpath(
                      "//*[@id=\"login-password\"]/input[@type=\"password\" and"
                          + " @placeholder=\"Passwort\"]")));

      onWebView().withElement(findElement(Locator.ID, "password-checkbox")).perform(webClick());

      // html input type should change to text to display the password
      onWebView()
          .check(
              webContent(
                  hasElementWithXpath(
                      "//*[@id=\"login-password\"]/input[@type=\"text\" and"
                          + " @placeholder=\"Passwort\"]")));

      onWebView().withElement(findElement(Locator.ID, "password-checkbox")).perform(webClick());

      // html input type change back to password to hide the text
      onWebView()
          .check(
              webContent(
                  hasElementWithXpath(
                      "//*[@id=\"login-password\"]/input[@type=\"password\" and"
                          + " @placeholder=\"Passwort\"]")));
    }
  }

  @Nested
  @Order(2)
  class CopyButtonTest {
    ClipboardManager clipboard;

    @BeforeEach
    void initClipBoard() {
      clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Test
    void copySuccess() throws Exception {
      // go to website detail page
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      // wait for page to load
      Thread.sleep(loadDelay);

      onWebView().withElement(findElement(Locator.ID, "password-copy")).perform(webClick());
      Thread.sleep(loadDelay);

      // toast should be displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
      onWebView().check(webContent(containingTextInBody("Passwort wurde kopiert")));

      // password should be copied in clipboard
      String copiedPassword = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
      assertEquals(defaultLoginPassword, copiedPassword);

      // clipboard clear after 20 seconds
      Thread.sleep(20000);
      if (clipboard.getPrimaryClip() != null) {
        String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        assertEquals("", actualText);
        assertEquals(1, clipboard.getPrimaryClip().getItemCount());
      }
    }
  }

  @Nested
  @Order(4)
  class GenerateButtonTest {
    @Test
    void inAddWebsitePage() throws Exception {
      // go to add website page
      onWebView().withElement(findElement(Locator.ID, "add-button")).perform(webClick());
      // wait for page to load
      Thread.sleep(loadDelay);

      onWebView().check(webContent(containingTextInBody("Neues Passwort")));
      testGenerateButton();
    }

    @Test
    void inWebsiteEditPage() throws Exception {
      // go to website edit page
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      onWebView().withElement(findElement(Locator.ID, "edit-button")).perform(webClick());
      Thread.sleep(loadDelay);

      onWebView().check(webContent(hasElementWithId("save-button")));
      // clear the fields
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
          .perform(clearElement());
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(clearElement());
      testGenerateButton();
    }

    void testGenerateButton() throws Exception {
      onWebView()
          .withElement(findElement(Locator.ID, "generate-random-username"))
          .perform(webClick());
      onWebView()
          .withElement(findElement(Locator.ID, "generate-random-password"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      // check the generated strings

      scenario.onActivity(
          activity -> {
            WebView webview = getWebViewInstance(activity);

            webview.evaluateJavascript(
                "document.querySelector('#login-name').value",
                s -> {
                  int stringLength = 8;
                  String actual = parseJSONtoString(s);
                  assertEquals(stringLength, actual.length());
                  assertNotEquals(defaultLoginName, actual);
                });

            webview.evaluateJavascript(
                "document.querySelector('#login-password').value",
                s -> {
                  int stringLength = 12;
                  String actual = parseJSONtoString(s);
                  assertEquals(stringLength, actual.length());
                  assertNotEquals(defaultLoginPassword, actual);
                });
          });

      // check identity function
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(clearElement())
          .perform(webKeys(defaultLoginPassword));
      onWebView()
          .withElement(findElement(Locator.ID, "generate-random-password"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      // check the generated strings
      scenario.onActivity(
          activity -> {
            WebView webview = getWebViewInstance(activity);

            webview.evaluateJavascript(
                "document.querySelector('#login-password').value",
                s -> {
                  int stringLength = 12 + defaultLoginPassword.length();
                  String actual = parseJSONtoString(s);
                  String sub = actual.substring(0, defaultLoginPassword.length());
                  assertEquals(defaultLoginPassword, sub);
                  assertEquals(stringLength, actual.length());
                  assertNotEquals(defaultLoginPassword, actual);
                });
          });
    }
  }

  @Nested
  @Order(5)
  class BackButtonTest {

    @BeforeEach
    void startWebsiteDetailPage() throws Exception {
      // go to the saved password
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      Thread.sleep(loadDelay);
    }

    @Test
    void inWebsiteDetailPage() throws Exception {
      onWebView().withElement(findElement(Locator.XPATH, "//ons-back-button")).perform(webClick());
      Thread.sleep(loadDelay);
      // check return back to overview
      onWebView().check(webContent(hasElementWithId("add-button")));
    }

    @Test
    void inWebsiteEditPage() throws Exception {
      onWebView().withElement(findElement(Locator.ID, "edit-button")).perform(webClick());
      Thread.sleep(loadDelay);
      onWebView().withElement(findElement(Locator.XPATH, "//ons-back-button")).perform(webClick());
      Thread.sleep(loadDelay);
      // check return back to overview
      onWebView().check(webContent(hasElementWithId("add-button")));
    }
  }

  @Nested
  @Order(6)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class EditWebsiteTest {

    @BeforeEach
    void startEditWebsitePage() throws Exception {
      // go to the saved website to edit
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      onWebView().withElement(findElement(Locator.ID, "edit-button")).perform(webClick());
      Thread.sleep(loadDelay);
    }

    @Test
    @Order(1)
    void abortButtonTest() throws Exception {
      String newLoginName = generateRandomString(stringMaxLength);
      String newPassword = generateRandomString(stringMaxLength);

      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
          .perform(clearElement())
          .perform(webKeys(newLoginName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(clearElement())
          .perform(webKeys(newPassword));
      onWebView().withElement(findElement(Locator.ID, "abort-button")).perform(webClick());

      Thread.sleep(loadDelay);

      // check return back to website detail page and check old loginName and loginPassword
      onWebView().check(webContent(containingTextInBody(defaultWebsiteName)));
      checkLoginNamePasswordTextField(defaultLoginName, defaultLoginPassword);
    }

    @Test
    @Order(2)
    void editWebsiteSuccess() throws Exception {
      String newLoginName = generateRandomString(stringMaxLength);
      String newPassword = generateRandomString(stringMaxLength);

      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
          .perform(clearElement())
          .perform(webKeys(newLoginName));
      onWebView()
          .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
          .perform(clearElement())
          .perform(webKeys(newPassword));
      onWebView().withElement(findElement(Locator.ID, "save-button")).perform(webClick());

      // toast should be displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
      onWebView().check(webContent(containingTextInBody("nderungen gespeichert!")));

      Thread.sleep(loadDelay);

      // check return back to website detail page and check new loginName and loginPassword
      onWebView().check(webContent(containingTextInBody(defaultWebsiteName)));
      checkLoginNamePasswordTextField(newLoginName, newPassword);
    }
  }

  @Nested
  @Order(7)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class DeleteWebsiteTest {

    @BeforeEach
    void startDelete() throws Exception {
      // go to the saved password to edit
      onWebView()
          .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      // click delete button
      onWebView().withElement(findElement(Locator.ID, "delete-button")).perform(webClick());
      Thread.sleep(loadDelay);
      // alert dialog displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-alert-dialog")));
      onWebView().check(webContent(containingTextInBody("Passwort wirklich l")));
    }

    @Test
    @Order(1)
    void cancelledTest() {
      // click cancel
      onWebView()
          .withElement(
              findElement(
                  Locator.XPATH,
                  "/html/body/ons-alert-dialog/div[2]/div/div[3]/ons-alert-dialog-button[1]"))
          .perform(webClick());
      // stayed in website detail page
      onWebView().check(webContent(hasElementWithId("edit-button")));
      onWebView().check(webContent(containingTextInBody(defaultWebsiteName)));
    }

    @Test
    @Order(2)
    void deleteSuccess() throws Exception {
      // confirm delete
      onWebView()
          .withElement(
              findElement(
                  Locator.XPATH,
                  "/html/body/ons-alert-dialog/div[2]/div/div[3]/ons-alert-dialog-button[2]"))
          .perform(webClick());
      Thread.sleep(loadDelay);
      // toast should be displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
      onWebView().check(webContent(containingTextInBody("Passwort wurde gel")));

      // wait for onsenui element to be generated
      Thread.sleep(loadDelay);

      // return back to overview after deleting password
      onWebView().check(webContent(hasElementWithId("add-button")));
      // check element not exist
      onWebView()
          .check(webContent(hasNoElementWithXpath("//*[@id=\"overview\"]/ons-list-item[1]/div")));
    }
  }

  @Nested
  @Order(8)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class UrlTest {

    @Nested
    @Order(1)
    class InAddWebsitePage {

      @BeforeEach
      void init() throws Exception {
        // go to add password page
        onWebView().withElement(findElement(Locator.ID, "add-button")).perform(webClick());
        // wait for page to load
        Thread.sleep(loadDelay);

        onWebView().check(webContent(containingTextInBody("Neues Passwort")));

        onWebView()
            .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Webseite]"))
            .perform(webKeys(defaultWebsiteName));
        onWebView()
            .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Loginname]"))
            .perform(webKeys(defaultLoginName));
        onWebView()
            .withElement(findElement(Locator.CSS_SELECTOR, "input[placeholder=Passwort]"))
            .perform(webKeys(defaultLoginPassword));
        // add first url
        onWebView().withElement(findElement(Locator.ID, "add-url")).perform(webClick());
        onWebView()
            .withElement(
                findElement(
                    Locator.XPATH,
                    "//*[@id=\"url-items\"]/ons-list-item[1]/div[1]/ons-input/input"))
            .perform(webKeys(defaultWebAddress1));
        // add second url
        onWebView().withElement(findElement(Locator.ID, "add-url")).perform(webClick());
        onWebView()
            .withElement(
                findElement(
                    Locator.XPATH,
                    "//*[@id=\"url-items\"]/ons-list-item[2]/div[1]/ons-input/input"))
            .perform(webKeys(defaultWebAddress2));
      }

      @Test
      void addUrlTest() throws Exception {
        onWebView().withElement(findElement(Locator.ID, "commit-button")).perform(webClick());

        // toast should be displayed
        onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
        onWebView().check(webContent(containingTextInBody("Passwort hinzu")));

        // wait for onsenui element to be generated
        Thread.sleep(loadDelay);

        // return back to overview after adding password
        onWebView().check(webContent(hasElementWithId("add-button")));
        // check element exist
        onWebView()
            .check(webContent(containingTextInBody(defaultWebsiteName + " / " + defaultLoginName)));

        // go to the saved password to check loginName and loginPassword and url
        onWebView()
            .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
            .perform(webClick());
        Thread.sleep(loadDelay);
        checkLoginNamePasswordTextField(defaultLoginName, defaultLoginPassword);
        onWebView().check(webContent(containingTextInBody(defaultWebAddress1)));
        onWebView().check(webContent(containingTextInBody(defaultWebAddress2)));
        onWebView()
            .check(webContent(hasElementWithXpath("//*[@id=\"url-items\"]/ons-list-item/div[1]")));
        onWebView()
            .check(webContent(hasElementWithXpath("//*[@id=\"url-items\"]/ons-list-item/div[2]")));
      }

      @Test
      void deleteUrlButtonTest() {
        onWebView()
            .withElement(
                findElement(
                    Locator.XPATH, "//*[@id=\"url-items\"]/ons-list-item[2]/div[2]/ons-icon"))
            .perform(webClick());
        onWebView()
            .check(
                webContent(
                    hasNoElementWithXpath(
                        "//*[@id=\"url-items\"]/ons-list-item[2]/div[1]/ons-input/input")));
      }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class InEditWebsitePage {

      @BeforeEach
      void init() throws Exception {
        // go to the saved password to edit
        onWebView()
            .withElement(findElement(Locator.XPATH, "//*[@id=\"overview\"]/ons-list-item[1]/div"))
            .perform(webClick());
        Thread.sleep(loadDelay);
        onWebView().withElement(findElement(Locator.ID, "edit-button")).perform(webClick());
        Thread.sleep(loadDelay);
      }

      @Test
      @Order(1)
      void deleteUrlButtonTest() throws Exception {
        // delete the second url
        onWebView()
            .withElement(
                findElement(
                    Locator.XPATH, "//*[@id=\"url-items\"]/ons-list-item[2]/div[2]/ons-icon"))
            .perform(webClick());
        onWebView()
            .check(
                webContent(
                    hasNoElementWithXpath(
                        "//*[@id=\"url-items\"]/ons-list-item[2]/div[1]/ons-input/input")));
        onWebView().withElement(findElement(Locator.ID, "save-button")).perform(webClick());
        Thread.sleep(loadDelay);

        // toast should be displayed
        onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
        onWebView().check(webContent(containingTextInBody("nderungen gespeichert!")));

        // check return back to password detail page and check url
        onWebView().check(webContent(hasElementWithId("edit-button")));
        onWebView()
            .check(webContent(hasElementWithXpath("//*[@id=\"url-items\"]/ons-list-item/div[2]")));
      }

      @Test
      @Order(2)
      void addUrlTest() throws Exception {
        // add second url again
        onWebView().withElement(findElement(Locator.ID, "add-url")).perform(webClick());
        onWebView()
            .withElement(
                findElement(
                    Locator.XPATH,
                    "//*[@id=\"url-items\"]/ons-list-item[2]/div[1]/ons-input/input"))
            .perform(webKeys(defaultWebAddress2));
        onWebView().withElement(findElement(Locator.ID, "save-button")).perform(webClick());
        Thread.sleep(loadDelay);

        // toast should be displayed
        onWebView().check(webContent(hasElementWithXpath("/html/body/ons-toast/div/div")));
        onWebView().check(webContent(containingTextInBody("nderungen gespeichert!")));

        // check return back to password detail page and check url
        onWebView().check(webContent(hasElementWithId("edit-button")));
        onWebView().check(webContent(containingTextInBody(defaultWebAddress2)));
        onWebView()
            .check(webContent(hasElementWithXpath("//*[@id=\"url-items\"]/ons-list-item/div[2]")));
      }
    }
  }

  @Nested
  @Order(9)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class DeleteUserTest {
    @BeforeEach
    void startDelete() throws Exception {
      // click delete button
      onWebView().withElement(findElement(Locator.ID, "more-button")).perform(webClick());
      onWebView().withElement(findElement(Locator.ID, "delete-user")).perform(webClick());
      Thread.sleep(loadDelay);
      // alert dialog displayed
      onWebView().check(webContent(hasElementWithXpath("/html/body/ons-alert-dialog")));
      onWebView().check(webContent(containingTextInBody("Benutzer wirklich l")));
    }

    @Test
    @Order(1)
    void cancelledTest() {
      // click cancel
      onWebView()
          .withElement(
              findElement(
                  Locator.XPATH,
                  "/html/body/ons-alert-dialog/div[2]/div/div[3]/ons-alert-dialog-button[1]"))
          .perform(webClick());
      // stayed in overview page
      onWebView()
          .check(webContent(containingTextInBody(defaultWebsiteName + " / " + defaultLoginName)));
      onWebView().check(webContent(hasElementWithId("add-button")));
    }

    @Test
    @Order(2)
    void deleteSuccess() throws Exception {
      // confirm delete
      onWebView()
          .withElement(
              findElement(
                  Locator.XPATH,
                  "/html/body/ons-alert-dialog/div[2]/div/div[3]/ons-alert-dialog-button[2]"))
          .perform(webClick());
      Thread.sleep(loadDelay);

      // check return back to login page
      onWebView().check(webContent(containingTextInBody("Anmeldung")));
      onWebView().check(webContent(hasElementWithId("login")));
      // check user not exist
      onWebView()
          .withElement(findElement(Locator.ID, "input-username"))
          .perform(webKeys(defaultUsername));
      onWebView()
          .withElement(findElement(Locator.ID, "input-userPassword"))
          .perform(webKeys(defaultUserPassword));
      onWebView().withElement(findElement(Locator.ID, "login")).perform(webClick());
      onWebView()
          .check(webContent(containingTextInBody("ltiger Benutzername oder falsches Passwort")));
    }
  }

  void checkLoginNamePasswordTextField(String loginName, String loginPassword) {
    scenario.onActivity(
        activity -> {
          WebView webview = getWebViewInstance(activity);

          webview.evaluateJavascript(
              "document.querySelector('#login-name').value",
              s -> {
                String actual = parseJSONtoString(s);
                assertEquals(loginName, actual);
              });

          webview.evaluateJavascript(
              "document.querySelector('#login-password').value",
              s -> {
                String actual = parseJSONtoString(s);
                assertEquals(loginPassword, actual);
              });
        });
  }

  String parseJSONtoString(String s) {
    return s.substring(1, s.length() - 1).replace("\\\\", "\\").replace("\\\"", "\"");
  }

  @AfterAll
  static void tearDown() {
    mainActivity.deleteDatabase("testDatabase");
  }
}
