package tu.bp21.passwortmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import tu.bp21.passwortmanager.js_interfaces.InterfaceCrypto;
import tu.bp21.passwortmanager.js_interfaces.InterfacePassword;
import tu.bp21.passwortmanager.js_interfaces.InterfaceTools;
import tu.bp21.passwortmanager.js_interfaces.InterfaceUser;
import tu.bp21.passwortmanager.js_interfaces.InterfaceWebsite;
import tu.bp21.passwortmanager.db.ApplicationDatabase;

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

    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    // enable WebViews inspection with chrome://inspect
    WebView.setWebContentsDebuggingEnabled(true);

    ApplicationDatabase database =
        Room.databaseBuilder(this, ApplicationDatabase.class, "database")
            .allowMainThreadQueries()
            .build();

    InterfaceUser jsiUser = new InterfaceUser(database.getUserDao());
    InterfacePassword jsiPassword = new InterfacePassword(database.getPasswordDao());
    InterfaceWebsite jsiWebsite = new InterfaceWebsite(database.getWebsiteDao());
    InterfaceCrypto jsiCrypto = new InterfaceCrypto(database.getUserDao());
    Crypto.setPasswordDao(database.getPasswordDao());

    InterfaceTools jsiTools = new InterfaceTools(this);

    webView = new WebView(this);
    webView.setWebViewClient(new AssetWebViewClient(this));

    webView.getSettings().setJavaScriptEnabled(true);

    webView.addJavascriptInterface(jsiUser, "Java_InterfaceUser");
    webView.addJavascriptInterface(jsiPassword, "Java_InterfacePassword");
    webView.addJavascriptInterface(jsiWebsite, "Java_InterfaceWebsite");
    webView.addJavascriptInterface(jsiCrypto, "Java_InterfaceCrypto");

    webView.addJavascriptInterface(jsiTools, "Java_InterfaceTools");

    webView.loadUrl("https://appassets.androidplatform.net/assets/src/html/index.html");

    setContentView(webView);
  }

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.evaluateJavascript(
          "back()",
          value -> {
            if (value != null) webView.goBack();
          });
    } else {
      super.onBackPressed();
    }
  }
}
