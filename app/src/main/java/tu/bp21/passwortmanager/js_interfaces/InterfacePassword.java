package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import java.util.ArrayList;

import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.Password;

public class InterfacePassword {
  private final PasswordDao passwordDataAccessObject;

  public InterfacePassword(PasswordDao passwordDao) {
    this.passwordDataAccessObject = passwordDao;
  }

  @JavascriptInterface
  public boolean createPassword(String username, String website, String loginName, String plainPassword) {
    byte[] cipherPassword = Crypto.encrypt(plainPassword);
    Password newPassword = new Password(username, website, loginName, cipherPassword);

    try {
      passwordDataAccessObject.addPassword(newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean updatePassword(String username, String website, String loginName, String plainPassword) {
    byte[] cipherPassword = Crypto.encrypt(plainPassword);
    Password newPassword = new Password(username, website, loginName, cipherPassword);

    try {
      passwordDataAccessObject.updatePassword(newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deletePassword(String user, String website) {
    Password newPassword = new Password(user, website);
    try {
      if (passwordDataAccessObject.deletePassword(newPassword) == 0)
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
    passwordDataAccessObject.getPasswordList(user).forEach(x -> list.add(x.toString()));
    return "{\"dataArray\":" + list.toString() + "}";
  }

  @JavascriptInterface
  public String getLoginName(String user, String password, String id) {
    return passwordDataAccessObject.getPassword(user, id).loginName;
  }

  @JavascriptInterface
  public String getPassword(String user, String password, String id) {
    byte[] cipherPassword = passwordDataAccessObject.getPassword(user, id).password;
    String plainPassword = Crypto.decrypt(cipherPassword);
    return plainPassword;
  }
}
