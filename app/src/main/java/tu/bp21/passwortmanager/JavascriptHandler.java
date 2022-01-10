package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;
import java.util.ArrayList;

import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.PasswordDao;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.Website;

/** Framework that can be used in JavaScript. */
public class JavascriptHandler {
  private final PasswordDao passwordDao;

  public JavascriptHandler(PasswordDao dao) {
    passwordDao = dao;
  }

  @JavascriptInterface
  public boolean existUser(String user) {
    return passwordDao.getUser(user) != null;
  }

  @JavascriptInterface
  public boolean checkUser(String username, String hash) {
    User user = passwordDao.getUser(username);
    return user != null && user.password.equals(hash);
  }

  @JavascriptInterface
  public boolean createUser(String user, String email, String hash) {
    User newUser = new User(user, email, hash);

    try {
      passwordDao.addUser(newUser);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean createPassword(String user, String website, String loginName, String password) {
    Password newPassword = new Password(user, website, loginName, password);

    try {
      passwordDao.addPassword(newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean updatePassword(String user, String website, String loginName, String password) {
    Password newPassword = new Password(user, website, loginName, password);

    try {
      passwordDao.updatePassword(newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deletePassword(String user, String website, String loginName, String password) {
    Password newPassword = new Password(user, website, loginName, password);
    try {
      passwordDao.deletePassword(newPassword);//TODO: DELETE gibt int zurück
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public String getPasswordList(String user, String hash) {
    ArrayList<String> list = new ArrayList<>();
    passwordDao.getPasswordList(user).forEach(x -> list.add(x.toString()));
    return "{\"dataArray\":" + list.toString() + "}";
  }

  @JavascriptInterface
  public boolean saveUrl(String user, String website, String url) {
    Website newUrl = new Website(user, website, url);

    try {
      passwordDao.addWebsite(newUrl);
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
      passwordDao.deleteWebsite(newUrl);//TODO: DELETE gibt int zurück
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public String getUrlList(String user, String website){
    ArrayList<String> list = new ArrayList<>();
    passwordDao.getWebsiteList(user, website).forEach(x -> list.add(x.toString()));
    return "{\"dataArray\":" + list.toString() + "}";
  }
}
