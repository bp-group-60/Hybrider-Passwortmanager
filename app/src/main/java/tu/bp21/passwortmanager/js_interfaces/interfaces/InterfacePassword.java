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
      String username, String websiteName, String loginName, String plainUserPassword, String key) {
    if (websiteName.equals("")) {
      return false;
    }

    Password newPasswordItem =
        createEncryptedPassword(username, websiteName, loginName, plainUserPassword, key);

    try {
      passwordDataAccessObject.addPassword(newPasswordItem);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public boolean updatePassword(
      String username, String websiteName, String loginName, String plainUserPassword, String key) {
    Password newPasswordItem =
        createEncryptedPassword(username, websiteName, loginName, plainUserPassword, key);

    try {
      if (passwordDataAccessObject.updatePassword(newPasswordItem) == 0) {
        throw new RuntimeException("nothing was updated");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public boolean deletePassword(String username, String websiteName) {
    Password newPasswordItem = new Password(username, websiteName);
    try {
      if (passwordDataAccessObject.deletePassword(newPasswordItem) == 0) {
        throw new RuntimeException("nothing was deleted");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public String getPasswordOverviewList(String username) {
    List<Password> list = passwordDataAccessObject.getPasswordList(username);
    return "{\"dataArray\":" + list.toString() + "}";
  }

  @JavascriptInterface
  public String getLoginName(String username, String websiteName) {
    Password queryResult = passwordDataAccessObject.getPassword(username, websiteName);

    if (queryResult == null) {
      return "";
    }

    return queryResult.loginName;
  }

  @JavascriptInterface
  public String getPassword(String username, String websiteName, String key) {
    Password queryResult = passwordDataAccessObject.getPassword(username, websiteName);

    if (queryResult == null) {
      return "";
    }

    byte[] cipherPassword = queryResult.password;
    byte[] associatedData = (username + websiteName).getBytes();

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
      String username, String websiteName, String loginName, String plainUserPassword, String key) {
    int ivSize = 12;
    ArrayList<String> ivList = getIVList(username, ivSize);
    byte[] iv = Crypto.generateUniqueIV(ivList, ivSize);
    byte[] associatedData = (username + websiteName).getBytes();
    byte[] cipherPassword =
        Crypto.encrypt(plainUserPassword, associatedData, BaseEncoding.base16().decode(key), iv);
    return new Password(username, websiteName, loginName, cipherPassword);
  }
}
