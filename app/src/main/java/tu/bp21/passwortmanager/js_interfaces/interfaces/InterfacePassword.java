package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tu.bp21.passwortmanager.cryptography.Crypto;
import tu.bp21.passwortmanager.db.data_access_objects.PasswordDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Password;

public class InterfacePassword {
  private final PasswordDataAccessObject passwordDataAccessObject;

  public InterfacePassword(PasswordDataAccessObject passwordDataAccessObject) {
    this.passwordDataAccessObject = passwordDataAccessObject;
  }

  @JavascriptInterface
  public boolean createPassword(
      String username, String website, String loginName, String plainPassword, String key) {
    if (website.equals("")) {
      return false;
    }

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
      if (passwordDataAccessObject.updatePassword(newPassword) == 0) {
        throw new RuntimeException("nothing was updated");
      }
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
      if (passwordDataAccessObject.deletePassword(newPassword) == 0) {
        throw new RuntimeException("nothing was deleted");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public String getPasswordOverviewList(String user) {
    List<Password> list = passwordDataAccessObject.getPasswordList(user);
    return "{\"dataArray\":" + list.toString() + "}";
  }

  @JavascriptInterface
  public String getLoginName(String user, String website) {
    Password queryResult = passwordDataAccessObject.getPassword(user, website);

    if (queryResult == null) {
      return "";
    }

    return queryResult.loginName;
  }

  @JavascriptInterface
  public String getPassword(String user, String website, String key) {
    Password queryResult = passwordDataAccessObject.getPassword(user, website);

    if (queryResult == null) {
      return "";
    }

    byte[] cipherPassword = queryResult.password;
    byte[] associatedData = (user + website).getBytes();

    return Crypto.decrypt(cipherPassword, associatedData, BaseEncoding.base16().decode(key));
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
