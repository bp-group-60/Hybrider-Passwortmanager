package tu.bp21.passwortmanager.web_view_client;

import android.content.Context;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.webkit.WebViewAssetLoader;

public class AssetWebViewClient extends WebViewClient {

  private final WebViewAssetLoader assetLoader;

  public AssetWebViewClient(Context context) {
    assetLoader =
        // default assets folder helper class
        new WebViewAssetLoader.Builder()
            // used to define path in url
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
            .build();
  }

  @Override
  public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
    WebResourceResponse intercepted = assetLoader.shouldInterceptRequest(request.getUrl());

    if (intercepted != null) {
      if (request.getUrl().toString().endsWith("js")) {
        intercepted.setMimeType("text/javascript");
      } else if (request.getUrl().toString().endsWith("wasm")) {
        intercepted.setMimeType("application/wasm");
      }
    }

    return intercepted;
  }
}
