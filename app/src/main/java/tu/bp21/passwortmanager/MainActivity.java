package tu.bp21.passwortmanager;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.nio.charset.StandardCharsets;

import tu.bp21.passwortmanager.db.PasswordDatabase;

/** Main entry point for app. */
public class MainActivity extends AppCompatActivity {
  private WebView webView;
  static {
       System.loadLibrary("Crypto");
    }

  @Override
  @SuppressLint("SetJavaScriptEnabled")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /*
    String input = "Rightig verschluesselt";
    String password = "abcdef123";
    Crypto.setSalt(Crypto.generateSalt(16));
    Crypto.setKey(password);

    byte[] cipher = Crypto.encrypt(input);
    String text = Crypto.decrypt(cipher);
    System.out.println(text);
    */
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    // if your build is in debug mode, enable WebViews inspection
    /*if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
      WebView.setWebContentsDebuggingEnabled(true);
    }*/

    PasswordDatabase database =
        Room.databaseBuilder(this, PasswordDatabase.class, "database")
            .allowMainThreadQueries()
            .build();

    webView = new WebView(this);
    webView.setWebViewClient(new AssetWebViewClient(this));

    webView.getSettings().setJavaScriptEnabled(true);
    webView.addJavascriptInterface(new JavascriptHandler(database.getDao()), "Java");
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


}
