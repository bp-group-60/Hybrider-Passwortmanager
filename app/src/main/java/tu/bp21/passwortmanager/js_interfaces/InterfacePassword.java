package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import java.util.ArrayList;

import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.Password;

public class InterfacePassword {
  private final PasswordDao passwordDao;

  public InterfacePassword(PasswordDao passwordDao) {
    this.passwordDao = passwordDao;
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
  public boolean deletePassword(String user, String website) {
    Password newPassword = new Password(user, website, "", "");
    try {
      if (passwordDao.deletePassword(newPassword) == 0)
        throw new RuntimeException("nothing was deleted");
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
  public String getLoginName(String user, String password, String id) {
    return passwordDao.getPassword(user, id).loginName;
  }

  @JavascriptInterface
  public String getPassword(String user, String password, String id) {
    return passwordDao.getPassword(user, id).password;
  }
}
