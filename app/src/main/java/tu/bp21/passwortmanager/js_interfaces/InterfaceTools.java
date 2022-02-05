package tu.bp21.passwortmanager.js_interfaces;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.JavascriptInterface;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class InterfaceTools {
  private final ClipboardManager clipboard;
  private final WorkManager workManager;

  public InterfaceTools(Context context) {
    clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    workManager = WorkManager.getInstance(context);
  }

  @JavascriptInterface
  public void copyToClipboard(String text, long timeout) {
    String lable = "Hybrider-Passwormanager:" + System.currentTimeMillis();
    clipboard.setPrimaryClip(ClipData.newPlainText(lable, text));

    if (timeout >= 0) {
      Data.Builder data = new Data.Builder();
      data.putString("lable", lable);

      OneTimeWorkRequest clipboardTimeoutRequest =
          new OneTimeWorkRequest.Builder(ClipboardTimeoutWorker.class)
              .setInitialDelay(timeout, MILLISECONDS)
              .setInputData(data.build())
              .build();

      workManager.beginWith(clipboardTimeoutRequest).enqueue();
    }
  }
}
