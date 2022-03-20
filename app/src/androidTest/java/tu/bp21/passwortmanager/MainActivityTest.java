package tu.bp21.passwortmanager;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import static androidx.test.espresso.web.matcher.DomMatchers.hasElementWithId;
import static androidx.test.espresso.web.matcher.DomMatchers.hasElementWithXpath;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webKeys;
import static androidx.test.espresso.web.assertion.WebViewAssertions.*;
import static androidx.test.espresso.web.matcher.DomMatchers.containingTextInBody;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import static tu.bp21.passwortmanager.StringFunction.generateRandomString;
import static tu.bp21.passwortmanager.WebViewFunction.getWebViewInstance;

import android.webkit.WebView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.List;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import tu.bp21.passwortmanager.web_view_client.AssetWebViewClient;

class MainActivityTest {

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  ActivityScenario<MainActivity> scenario;
  static MainActivity mainActivity;
  static final String databaseName = "database";

  @BeforeEach
  void setUp() throws Exception {
    if (mainActivity == null) {
      scenarioExtension.getScenario().onActivity(activity -> mainActivity = activity);
    }

    scenario = scenarioExtension.getScenario();
  }

  @AfterEach
  void tearDown() throws Exception {}

  @Test
  void onCreateTest() {
    scenario.onActivity(
            activity -> {
              WebView webview = getWebViewInstance(activity);
              assertNotNull(webview);

              String expectedUrl = "https://appassets.androidplatform.net/assets/src/html/index.html";
              String actualUrl = webview.getUrl();
              assertEquals(expectedUrl, actualUrl);

              assertTrue(webview.getSettings().getJavaScriptEnabled());
              assertTrue(webview.getWebViewClient() instanceof AssetWebViewClient);

              List<String> dbList = Arrays.asList(activity.databaseList());
              assertTrue(dbList.contains(databaseName));
            });
  }
}
