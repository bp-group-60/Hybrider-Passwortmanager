package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import java.util.ArrayList;

import tu.bp21.passwortmanager.db.dao.WebsiteDao;
import tu.bp21.passwortmanager.db.Website;

public class InterfaceWebsite {
  private final WebsiteDao websiteDao;

  public InterfaceWebsite(WebsiteDao websiteDao) {
    this.websiteDao = websiteDao;
  }

  @JavascriptInterface
  public boolean saveUrl(String user, String website, String url) {
    Website newUrl = new Website(user, website, url);

    try {
      websiteDao.addWebsite(newUrl);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deleteUrl(String user, String website, String url) {
    Website newUrl = new Website(user, website, url);

    try {
      if (websiteDao.deleteWebsite(newUrl) == 0) throw new RuntimeException("nothing was deleted");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public String getUrlList(String user, String website) {
    ArrayList<String> list = new ArrayList<>();
    websiteDao.getWebsiteList(user, website).forEach(x -> list.add(x.toString()));
    return "{\"dataArray\":" + list.toString() + "}";
  }
}
