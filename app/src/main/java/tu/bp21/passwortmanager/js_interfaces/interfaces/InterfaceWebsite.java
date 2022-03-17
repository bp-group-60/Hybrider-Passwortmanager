package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import java.util.List;

import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Website;

public class InterfaceWebsite {
  private final WebsiteDataAccessObject websiteDataAccessObject;

  public InterfaceWebsite(WebsiteDataAccessObject websiteDataAccessObject) {
    this.websiteDataAccessObject = websiteDataAccessObject;
  }

  @JavascriptInterface
  public boolean saveUrl(String username, String websiteName, String url) {
    if (url.equals("")) {
      return false;
    }

    Website newUrl = new Website(username, websiteName, url);

    try {
      websiteDataAccessObject.addWebsite(newUrl);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deleteUrl(String username, String websiteName, String url) {
    Website urlToDelete = new Website(username, websiteName, url);

    try {
      if (websiteDataAccessObject.deleteWebsite(urlToDelete) == 0) {
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
    List<Website> list = websiteDataAccessObject.getWebsiteList(username, websiteName);
    return "{\"dataArray\":" + list.toString() + "}";
  }
}
