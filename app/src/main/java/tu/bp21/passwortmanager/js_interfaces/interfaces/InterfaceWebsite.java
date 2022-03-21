package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tu.bp21.passwortmanager.Constants;
import tu.bp21.passwortmanager.cryptography.Crypto;
import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Website;

public class InterfaceWebsite {
  private final WebsiteDataAccessObject websiteDataAccessObject;

  public InterfaceWebsite(WebsiteDataAccessObject websiteDataAccessObject) {
    this.websiteDataAccessObject = websiteDataAccessObject;
  }

  @JavascriptInterface
  public boolean createWebsite(
      String username, String websiteName, String loginName, String loginPassword, String key) {
    if (websiteName.equals("")) {
      return false;
    }

    byte[] encryptedLoginPassword =
        createEncryptedPassword(username, websiteName, loginPassword, key);
    Website newWebsiteItem = new Website(username, websiteName, loginName, encryptedLoginPassword);

    try {
      websiteDataAccessObject.addWebsite(newWebsiteItem);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public boolean updateWebsite(
      String username, String websiteName, String loginName, String loginPassword, String key) {
    byte[] encryptedLoginPassword =
        createEncryptedPassword(username, websiteName, loginPassword, key);
    Website newWebsiteItem = new Website(username, websiteName, loginName, encryptedLoginPassword);

    try {
      if (websiteDataAccessObject.updateWebsite(newWebsiteItem) == 0) {
        throw new RuntimeException("nothing was updated");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public boolean deleteWebsite(String username, String websiteName) {
    Website newWebsiteItem = new Website(username, websiteName);
    try {
      if (websiteDataAccessObject.deleteWebsite(newWebsiteItem) == 0) {
        throw new RuntimeException("nothing was deleted");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public String getWebsiteOverviewList(String username) {
    List<Website> list = websiteDataAccessObject.getWebsiteList(username);
    return "{\"dataArray\":" + list.toString() + "}";
  }

  @JavascriptInterface
  public String getLoginName(String username, String websiteName) {
    Website queryResult = websiteDataAccessObject.getWebsite(username, websiteName);

    if (queryResult == null) {
      return "";
    }

    return queryResult.loginName;
  }

  @JavascriptInterface
  public String getLoginPassword(String username, String websiteName, String key) {
    Website queryResult = websiteDataAccessObject.getWebsite(username, websiteName);

    if (queryResult == null) {
      return "";
    }

    byte[] encryptedLoginPassword = queryResult.encryptedLoginPassword;
    byte[] associatedData = (username + websiteName).getBytes();

    return Crypto.decrypt(
        encryptedLoginPassword, associatedData, BaseEncoding.base16().decode(key));
  }

  private ArrayList<String> getIVList(String username, int ivSize) {
    List<Website> list = websiteDataAccessObject.getWebsiteList(username);
    ArrayList<String> ivList = new ArrayList<>();

    byte[] ivExisted;
    for (Website x : list) {
      ivExisted = Arrays.copyOf(x.encryptedLoginPassword, ivSize);
      ivList.add(BaseEncoding.base16().encode(ivExisted));
    }
    return ivList;
  }

  private byte[] createEncryptedPassword(
      String username, String websiteName, String loginPassword, String key) {
    int ivSize = Constants.ENCRYPT_IV_LENGTH;
    ArrayList<String> ivList = getIVList(username, ivSize);
    byte[] iv = Crypto.generateUniqueIV(ivList, ivSize);
    byte[] associatedData = (username + websiteName).getBytes();
    return Crypto.encrypt(loginPassword, associatedData, BaseEncoding.base16().decode(key), iv);
  }
}
