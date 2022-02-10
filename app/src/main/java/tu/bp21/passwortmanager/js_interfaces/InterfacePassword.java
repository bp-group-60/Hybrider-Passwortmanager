package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.Password;

public class InterfacePassword {
  private final PasswordDao passwordDataAccessObject;

  public InterfacePassword(PasswordDao passwordDao) {
    this.passwordDataAccessObject = passwordDao;
  }

  @JavascriptInterface
  public boolean createPassword(
      String username, String website, String loginName, String plainPassword, String key) {
    Password newPassword =
        createEncryptedPassword(username, website, loginName, plainPassword, key);

    try {
      passwordDataAccessObject.addPassword(newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public boolean updatePassword(
      String username, String website, String loginName, String plainPassword, String key) {
    Password newPassword =
        createEncryptedPassword(username, website, loginName, plainPassword, key);

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
  public String getPasswordList(String user) {
    List list = passwordDataAccessObject.getPasswordList(user);
    return "{\"dataArray\":" + list.toString() + "}";
  }

  @JavascriptInterface
  public String getLoginName(String user, String website) {
    return passwordDataAccessObject.getPassword(user, website).loginName;
  }

  @JavascriptInterface
  public String getPassword(String user, String website, String key) {
    byte[] cipherPassword = passwordDataAccessObject.getPassword(user, website).password;
    byte[] associatedData = (user + website).getBytes();
    String plainPassword =
        Crypto.decrypt(cipherPassword, associatedData, BaseEncoding.base16().decode(key));
    return plainPassword;
  }

  private ArrayList<String> getIVList(String username, int ivSize) {
    List<Password> list = passwordDataAccessObject.getPasswordList(username);
    ArrayList<String> ivList = new ArrayList<>();

    byte[] ivExisted;
    for (Password x : list) {
      ivExisted = Arrays.copyOf(x.password, ivSize);
      ivList.add(BaseEncoding.base16().encode(ivExisted));
    }
    return ivList;
  }

  private Password createEncryptedPassword(
      String username, String website, String loginName, String plainPassword, String key) {
    int ivSize = 12;
    ArrayList<String> ivList = getIVList(username, ivSize);
    byte[] iv = Crypto.generateUniqueIV(ivList, ivSize);
    byte[] associatedData = (username + website).getBytes();
    byte[] cipherPassword =
        Crypto.encrypt(plainPassword, associatedData, BaseEncoding.base16().decode(key), iv);
    return new Password(username, website, loginName, cipherPassword);
  }
}
