package tu.bp21.passwortmanager;

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
        new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
            .build();
  }

  @Override
  public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
    WebResourceResponse intercepted = assetLoader.shouldInterceptRequest(request.getUrl());

    if (intercepted != null && request.getUrl().toString().endsWith("js")) {
      intercepted.setMimeType("text/javascript");
    }

    return intercepted;
  }
}
