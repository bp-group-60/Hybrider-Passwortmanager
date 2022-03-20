package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import java.util.List;

import tu.bp21.passwortmanager.db.data_access_objects.UrlDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Url;

public class InterfaceUrl {
  private final UrlDataAccessObject urlDataAccessObject;

  public InterfaceUrl(UrlDataAccessObject urlDataAccessObject) {
    this.urlDataAccessObject = urlDataAccessObject;
  }

  @JavascriptInterface
  public boolean saveUrl(String username, String websiteName, String webAddress) {
    if (webAddress.equals("")) {
      return false;
    }

    Url newUrl = new Url(username, websiteName, webAddress);

    try {
      urlDataAccessObject.addUrl(newUrl);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deleteUrl(String username, String websiteName, String webAddress) {
    Url urlToDelete = new Url(username, websiteName, webAddress);

    try {
      if (urlDataAccessObject.deleteUrl(urlToDelete) == 0) {
        throw new RuntimeException("nothing was deleted");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public String getUrlList(String username, String websiteName) {
    List<Url> list = urlDataAccessObject.getUrlList(username, websiteName);
    return "{\"dataArray\":" + list.toString() + "}";
  }
}
