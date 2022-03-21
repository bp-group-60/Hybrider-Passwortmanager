package tu.bp21.passwortmanager.js_interfaces.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.*;
import static org.junit.jupiter.api.Assumptions.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

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

class InterfaceClipboardTest {
  static MainActivity mainActivity;
  ClipboardManager clipboard;

  InterfaceClipboard interfaceClipboard;
  static final int stringMaxLength = 100;
  static final int delay = 500;

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
    interfaceClipboard = new InterfaceClipboard(mainActivity);
  }

  @AfterEach
  void tearDown() {}

  @Nested
  @DisplayName("Tests for copyToClipboardWithTimeout")
  class CopyToClipboardWithTimeoutTest {

    @ParameterizedTest
    @CsvFileSource(
        resources = "/InterfaceClipboardTest/copyToClipboardWithTimeoutTest.csv",
        numLinesToSkip = 1)
    @DisplayName("Case: ")
    void copyToClipboardWithTimeoutSuccess(String displayCase, String text, long timeout) {

      assumingThat(
          timeout != 0,
          () -> {
            String expectedText = convertNullToEmptyString(text);
            interfaceClipboard.copyToClipboardWithTimeout(expectedText, timeout);
            Thread.sleep(delay);
            String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
            assertEquals(expectedText, actualText);
          });

      assumingThat(
          timeout >= 0,
          () -> {
            Thread.sleep(timeout);
            if (clipboard.getPrimaryClip() != null) {
              String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
              assertEquals("", actualText);
              assertEquals(1, clipboard.getPrimaryClip().getItemCount());
            }
          });
    }
  }

  @Test
  void clearClipboardTest() throws Exception {
    String textToCopy = generateRandomString(stringMaxLength);
    String label = generateRandomString(stringMaxLength);
    clipboard.setPrimaryClip(ClipData.newPlainText(label, textToCopy));
    interfaceClipboard.clearClipboard();
    Thread.sleep(delay);
    if (clipboard.getPrimaryClip() != null) {
      String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
      assertEquals("", actualText);
      assertEquals(1, clipboard.getPrimaryClip().getItemCount());
    }
  }
}
