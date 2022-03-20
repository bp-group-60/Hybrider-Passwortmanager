package tu.bp21.passwortmanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WebViewFunction {

  public static WebView getWebViewInstance(Activity activity) {
    // find the webview in the layout
    ViewGroup contentView = activity.findViewById(android.R.id.content);
    for (int i = 0; i < contentView.getChildCount(); i++) {
      View view = contentView.getChildAt(i);
      if (view instanceof WebView) return (WebView) view;
    }
    return null;
  }
}
