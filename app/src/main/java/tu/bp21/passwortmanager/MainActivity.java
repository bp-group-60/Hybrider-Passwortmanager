package tu.bp21.passwortmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
  private WebView webView;

  @Override
  @SuppressLint("SetJavaScriptEnabled")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
      this.getSupportActionBar().hide();
    } catch (NullPointerException e) {
    }

    // if your build is in debug mode, enable webviews inspection
    if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
      WebView.setWebContentsDebuggingEnabled(true);
    }

    webView = (WebView) findViewById(R.id.webview);
    webView.setWebViewClient(new WebViewClient());

    webView.loadUrl("file:///android_asset/index.html");

    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);

    /*final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
    		.addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
    		.addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
    		.build();
    webView.setWebViewClient(new LocalContentWebViewClient(assetLoader));*/
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
