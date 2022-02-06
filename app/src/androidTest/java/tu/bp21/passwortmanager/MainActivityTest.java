package tu.bp21.passwortmanager;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;

class MainActivityTest {

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  MainActivity mActivity;

  @BeforeEach
  void setUp() throws Exception {
    scenarioExtension.getScenario().onActivity(activity -> mActivity = activity);
  }

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
