package tu.bp21.passwortmanager;

import static androidx.test.espresso.web.sugar.Web.onWebView;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import androidx.test.filters.LargeTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;

@LargeTest
class MainActivityTest {

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  MainActivity mActivity;

  @BeforeEach
  void setUp() throws Exception {
    scenarioExtension.getScenario().onActivity(activity -> mActivity = activity);
    onWebView().forceJavascriptEnabled();
  }

  /*@Test
    void test() {
      onWebView().withElement(findElement(Locator.ID, "user")).perform(DriverAtoms.webKeys("abc"));
    }
  */
  @AfterEach
  void tearDown() throws Exception {}

  /*@Test
  void testonCreate() {
      View view  =  mActivity.getCurrentFocus();
      if(view instanceof WebView) assertNotNull((WebView)view);
      else assertFalse(true);
  }*/

  /*@Test
  void testonBackPressed() {

  }*/
}
