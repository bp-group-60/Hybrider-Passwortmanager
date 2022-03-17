package tu.bp21.passwortmanager.js_interfaces.helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ClipboardTimeoutWorker extends Worker {
  ClipboardManager clipboard;

  public ClipboardTimeoutWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
    clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
  }

  @NonNull
  @Override
  public Result doWork() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      clipboard.clearPrimaryClip();
    } else {
      clipboard.setPrimaryClip(ClipData.newPlainText("", ""));
    }
    int durationInMilliseconds = 1000;
    new Handler(Looper.getMainLooper())
        .postDelayed(
            () ->
                Toast.makeText(
                        getApplicationContext(), "Zwischenspeicher geleert", Toast.LENGTH_SHORT)
                    .show(),
                durationInMilliseconds);

    return Result.success();
  }
}
