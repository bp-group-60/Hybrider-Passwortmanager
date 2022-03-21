package tu.bp21.passwortmanager;

import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import static tu.bp21.passwortmanager.WebViewFunction.getWebViewInstance;

import android.webkit.WebView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.List;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.web_view_client.AssetWebViewClient;

class MainActivityTest {

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  ActivityScenario<MainActivity> scenario;
  static MainActivity mainActivity;
  static final String databaseName = "database";

  @BeforeEach
  void setUp() {
    if (mainActivity == null) {
      scenarioExtension.getScenario().onActivity(activity -> mainActivity = activity);
    }

    scenario = scenarioExtension.getScenario();
  }

  @AfterEach
  void tearDown() {}

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
