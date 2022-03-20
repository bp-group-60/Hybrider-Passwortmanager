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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

import de.mannodermaus.junit5.ActivityScenarioExtension;
import tu.bp21.passwortmanager.MainActivity;

class ClipboardTimeoutWorkerTest {
  static MainActivity mainActivity;
  ClipboardManager clipboard;

  WorkManager workManager;

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

  @AfterEach
  void tearDown() {}

  @Test
  void doWorkTest() throws Exception {
    int maxLength = 100;
    long timeInMilliseconds = 100;
    int expectedItemCount = 1;
    String textToCopy = generateRandomString(maxLength);
    String label = generateRandomString(maxLength);
    clipboard.setPrimaryClip(ClipData.newPlainText(label, textToCopy));
    OneTimeWorkRequest oneTimeWorkRequest = OneTimeWorkRequest.from(ClipboardTimeoutWorker.class);
    workManager.beginWith(oneTimeWorkRequest).enqueue();
    Thread.sleep(timeInMilliseconds);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      assertNull(clipboard.getPrimaryClip());
    } else {
      String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
      assertEquals("", actualText);
      assertEquals(expectedItemCount, clipboard.getPrimaryClip().getItemCount());
    }
  }
}
