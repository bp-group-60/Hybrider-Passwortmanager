package tu.bp21.passwortmanager.ui;

import androidx.test.espresso.web.webdriver.DriverAtoms;
import androidx.test.espresso.web.webdriver.Locator;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
//import static androidx.test.espresso.web.webdriver.DriverAtoms.
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import androidx.test.filters.LargeTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;

@LargeTest
class RegistrierenTest {

    @RegisterExtension
    final ActivityScenarioExtension<MainActivity> scenarioExtension =
            ActivityScenarioExtension.launch(MainActivity.class);

    MainActivity mActivity;

    @BeforeEach
    void setUp() throws Exception {
        scenarioExtension.getScenario().onActivity(activity -> mActivity = activity);
        onWebView().forceJavascriptEnabled();
        onWebView().withElement(findElement(Locator.ID, "registrieren"))
                .perform(webClick());
        //onWebView().withElement(findElement(Locator.ID,"konto-erstellen"))
        //        .check(webMatches());
    }

    @Test
    void test(){


    }

    @AfterEach
    void tearDown() throws Exception {}

}
