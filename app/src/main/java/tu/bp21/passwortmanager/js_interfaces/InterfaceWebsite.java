package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import java.util.List;

import tu.bp21.passwortmanager.db.dao.WebsiteDao;
import tu.bp21.passwortmanager.db.Website;

public class InterfaceWebsite {
  private final WebsiteDao websiteDataAccessObject;

  public InterfaceWebsite(WebsiteDao websiteDataAccessObject) {
    this.websiteDataAccessObject = websiteDataAccessObject;
  }

  @JavascriptInterface
  public boolean saveUrl(String user, String website, String url) {
    if (url.equals("")) {
      return false;
    }

    Website newUrl = new Website(user, website, url);

    try {
      websiteDataAccessObject.addWebsite(newUrl);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deleteUrl(String user, String website, String url) {
    Website urlToDelete = new Website(user, website, url);

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
  public String getUrlList(String user, String website) {
    List<Website> list = websiteDataAccessObject.getWebsiteList(user, website);
    return "{\"dataArray\":" + list.toString() + "}";
  }
}
