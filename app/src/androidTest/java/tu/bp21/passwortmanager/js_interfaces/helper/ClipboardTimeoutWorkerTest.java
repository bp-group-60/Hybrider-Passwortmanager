package tu.bp21.passwortmanager.js_interfaces.helper;

import static org.junit.jupiter.api.Assertions.*;

import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
  void doWork() throws Exception {
    String textToCopy = generateRandomString(100);
    String lable = generateRandomString(100);
    clipboard.setPrimaryClip(ClipData.newPlainText(lable, textToCopy));
    OneTimeWorkRequest oneTimeWorkRequest = OneTimeWorkRequest.from(ClipboardTimeoutWorker.class);
    workManager.beginWith(oneTimeWorkRequest).enqueue();
    //        while(true){
    //            if(workManager.getWorkInfoById(oneTimeWorkRequest.getId()).isDone())
    //                break;
    //        }
    Thread.sleep(100);
    String actualText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
    assertEquals("", actualText);
    assertEquals(1, clipboard.getPrimaryClip().getItemCount());
  }
}