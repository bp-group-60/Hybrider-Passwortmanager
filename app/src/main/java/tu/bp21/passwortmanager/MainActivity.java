package tu.bp21.passwortmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import tu.bp21.passwortmanager.js_interfaces.interfaces.InterfaceCrypto;
import tu.bp21.passwortmanager.js_interfaces.interfaces.InterfacePassword;
import tu.bp21.passwortmanager.js_interfaces.interfaces.InterfaceClipboard;
import tu.bp21.passwortmanager.js_interfaces.interfaces.InterfaceUser;
import tu.bp21.passwortmanager.js_interfaces.interfaces.InterfaceWebsite;
import tu.bp21.passwortmanager.db.database.ApplicationDatabase;
import tu.bp21.passwortmanager.web_view_client.AssetWebViewClient;

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

    ApplicationDatabase database =
        Room.databaseBuilder(this, ApplicationDatabase.class, "database")
            .allowMainThreadQueries()
            .build();

    InterfaceUser jsInterfaceUser = new InterfaceUser(database.getUserDataAccessObject());
    InterfacePassword jsInterfacePassword =
        new InterfacePassword(database.getPasswordDataAccessObject());
    InterfaceWebsite jsInterfaceWebsite =
        new InterfaceWebsite(database.getWebsiteDataAccessObject());
    InterfaceCrypto jsInterfaceCrypto = new InterfaceCrypto();

    InterfaceClipboard jsInterfaceClipboard = new InterfaceClipboard(this);

    webView = new WebView(this);
    webView.setWebViewClient(new AssetWebViewClient(this));

    webView.getSettings().setJavaScriptEnabled(true);

    webView.addJavascriptInterface(jsInterfaceUser, "Java_InterfaceUser");
    webView.addJavascriptInterface(jsInterfacePassword, "Java_InterfacePassword");
    webView.addJavascriptInterface(jsInterfaceWebsite, "Java_InterfaceWebsite");
    webView.addJavascriptInterface(jsInterfaceCrypto, "Java_InterfaceCrypto");

    webView.addJavascriptInterface(jsInterfaceClipboard, "Java_InterfaceTools");

    webView.loadUrl("https://appassets.androidplatform.net/assets/src/html/index.html");

    setContentView(webView);
  }

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.evaluateJavascript(
          " try {\n"
              + "  document.querySelector('#onsen-navigator').popPage()"
              + "    .catch( error => {"
              + "     sessionStorage.clear();"
              + "     history.back();"
              + "});"
              + "  } catch (e) {"
              + "    history.back();"
              + "  }",
          value -> {});
    } else {
      super.onBackPressed();
    }
  }
}
