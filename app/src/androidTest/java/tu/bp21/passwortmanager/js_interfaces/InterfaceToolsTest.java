package tu.bp21.passwortmanager.js_interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.*;
import static org.junit.jupiter.api.Assumptions.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;

class InterfaceToolsTest {
  static MainActivity mainActivity;
  ClipboardManager clipboard;

  InterfaceTools interfaceTools;

  @RegisterExtension
  final ActivityScenarioExtension<MainActivity> scenarioExtension =
      ActivityScenarioExtension.launch(MainActivity.class);

  @BeforeEach
  void setUp() {
    if (mainActivity == null) {
      ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
      scenario.onActivity(activity -> mainActivity = activity);
    }
    clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
    interfaceTools = new InterfaceTools(mainActivity);
  }

  @AfterEach
  void tearDown() {}

  @Nested
  @DisplayName("Tests for copyToClipboardWithTimeout")
  class copyToClipboardWithTimeoutTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/Tools/copyToClipboardWithTimeoutTest.csv", numLinesToSkip = 1)
    @DisplayName("Case: ")
    void copyToClipboardWithTimeoutSuccess(String displayCase, String text, long timeout)
        throws Exception {

      assumingThat(
          timeout != 0,
          () -> {
            String expectedText = convertNullToEmptyString(text);
            interfaceTools.copyToClipboardWithTimeout(expectedText, timeout);
            Thread.sleep(100);
            String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
            assertEquals(expectedText, actualText);
          });

      assumingThat(
          timeout >= 0,
          () -> {
            Thread.sleep(timeout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              assertNull(clipboard.getPrimaryClip());
            } else {
              String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
              assertEquals("", actualText);
              assertEquals(1, clipboard.getPrimaryClip().getItemCount());
            }
          });
    }
  }

  @Test
  void clearClipboardTest() throws Exception {
    String textToCopy = generateRandomString(100);
    String lable = generateRandomString(100);
    clipboard.setPrimaryClip(ClipData.newPlainText(lable, textToCopy));
    interfaceTools.clearClipboard();
    Thread.sleep(10);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      assertNull(clipboard.getPrimaryClip());
    } else {
      String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
      assertEquals("", actualText);
      assertEquals(1, clipboard.getPrimaryClip().getItemCount());
    }
  }
}
