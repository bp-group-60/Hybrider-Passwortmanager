package tu.bp21.passwortmanager.js_interfaces.helper;

import static org.junit.jupiter.api.Assertions.*;

import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;


import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;

class ClipboardTimeoutWorkerTest {
  static MainActivity mainActivity;
  ClipboardManager clipboard;

  WorkManager workManager;
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
    workManager = WorkManager.getInstance(mainActivity);
  }

  @Test
  void doWorkTest() throws Exception {
    int expectedItemCount = 1;
    String textToCopy = generateRandomString(stringMaxLength);
    String label = generateRandomString(stringMaxLength);
    clipboard.setPrimaryClip(ClipData.newPlainText(label, textToCopy));
    OneTimeWorkRequest oneTimeWorkRequest = OneTimeWorkRequest.from(ClipboardTimeoutWorker.class);
    workManager.beginWith(oneTimeWorkRequest).enqueue();
    Thread.sleep(delay);
    if (clipboard.getPrimaryClip() != null) {
      String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
      assertEquals("", actualText);
      assertEquals(expectedItemCount, clipboard.getPrimaryClip().getItemCount());
    }
  }
}
