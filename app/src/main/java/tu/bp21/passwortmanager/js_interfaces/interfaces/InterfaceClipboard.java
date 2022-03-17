package tu.bp21.passwortmanager.js_interfaces.interfaces;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.UUID;

import tu.bp21.passwortmanager.js_interfaces.helper.ClipboardTimeoutWorker;

public class InterfaceClipboard {
  private final ClipboardManager clipboard;
  private final WorkManager workManager;
  private UUID latestWorkId;

  public InterfaceClipboard(Context context) {
    clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    workManager = WorkManager.getInstance(context);
  }

  @JavascriptInterface
  public void copyToClipboardWithTimeout(String text, long timeout) {
    if (latestWorkId != null) workManager.cancelWorkById(latestWorkId);

    String lable = "Hybrider-Passwormanager:" + System.currentTimeMillis();
    clipboard.setPrimaryClip(ClipData.newPlainText(lable, text));

    long minimumTimeout = 0;
    if (timeout >= minimumTimeout) {
      Data.Builder data = new Data.Builder();
      data.putString("lable", lable);

      OneTimeWorkRequest clipboardTimeoutRequest =
          new OneTimeWorkRequest.Builder(ClipboardTimeoutWorker.class)
              .setInitialDelay(timeout, MILLISECONDS)
              .setInputData(data.build())
              .addTag("clearClipboard")
              .build();

      latestWorkId = clipboardTimeoutRequest.getId();
      workManager.beginWith(clipboardTimeoutRequest).enqueue();
    }
  }

  @JavascriptInterface
  public void clearClipboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      clipboard.clearPrimaryClip();
    } else {
      clipboard.setPrimaryClip(ClipData.newPlainText("", ""));
    }
  }
}
