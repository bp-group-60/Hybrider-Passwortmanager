package tu.bp21.passwortmanager;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import tu.bp21.passwortmanager.db.PasswordDatabase;

/** Main entry point for app. */
public class MainActivity extends AppCompatActivity {
  private WebView webView;
  private JavascriptHandler jsHandler;

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
      Room.databaseBuilder(this, PasswordDatabase.class, "database")
        .allowMainThreadQueries()
        .build();

    webView = new WebView(this);
    webView.setWebViewClient(new AssetWebViewClient(this));

    webView.getSettings().setJavaScriptEnabled(true);
    jsHandler = new JavascriptHandler(database.getDao());
    webView.addJavascriptInterface(jsHandler, "Java");
    webView.loadUrl("https://appassets.androidplatform.net/assets/src/html/index.html");


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

  public JavascriptHandler getJavascriptHandler(){
    return jsHandler;
  }
}