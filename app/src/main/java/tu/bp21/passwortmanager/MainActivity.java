package tu.bp21.passwortmanager;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import tu.bp21.passwortmanager.db.PasswordDatabase;

/** Main entry point for app. */
public class MainActivity extends AppCompatActivity {
  private WebView webView;

  @Override
  @SuppressLint("SetJavaScriptEnabled")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    // if your build is in debug mode, enable WebViews inspection
    if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
      WebView.setWebContentsDebuggingEnabled(true);
    }

    PasswordDatabase database =
        Room.databaseBuilder(getApplicationContext(), PasswordDatabase.class, "database")
            .allowMainThreadQueries()
            .build();

    webView = new WebView(getApplicationContext());
    webView.setWebViewClient(new WebViewClient());

    webView.addJavascriptInterface(new JavascriptHandler(database.getDao()), "Java");
    webView.loadUrl("file:///android_asset/index.html");

    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAllowUniversalAccessFromFileURLs(true);

    setContentView(webView);
  }

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.onBackPressed();
    }
  }
}
