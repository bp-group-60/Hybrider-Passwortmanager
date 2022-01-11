package tu.bp21.passwortmanager;

import static org.junit.Assert.*;

import android.view.View;
import android.webkit.WebView;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mMainActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testonCreate() {
        View view  =  mActivity.getCurrentFocus();
        if(view instanceof WebView) assertNotNull((WebView)view);
        else assertFalse(true);
    }

    @Test
    public void testonBackPressed() {
        
    }

    @Test
    public void testgetJavascriptHandler() {
    }
}