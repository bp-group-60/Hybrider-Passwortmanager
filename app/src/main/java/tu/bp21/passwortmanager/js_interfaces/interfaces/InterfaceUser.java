package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.Arrays;

import tu.bp21.passwortmanager.Constants;
import tu.bp21.passwortmanager.cryptography.Crypto;
import tu.bp21.passwortmanager.db.data_access_objects.UserDataAccessObject;
import tu.bp21.passwortmanager.db.entities.User;

public class InterfaceUser {
  private final UserDataAccessObject userDataAccessObject;

  public InterfaceUser(UserDataAccessObject userDataAccessObject) {
    this.userDataAccessObject = userDataAccessObject;
  }

  @JavascriptInterface
  public boolean existUser(String username) {
    return userDataAccessObject.getUser(username) != null;
  }

  @JavascriptInterface
  public boolean checkUser(String username, String hashedUserPassword) {
    User userEntity = userDataAccessObject.getUser(username);
    if (userEntity != null) {
      byte[] givenHash = BaseEncoding.base16().decode(hashedUserPassword.toUpperCase());
      return Arrays.equals(userEntity.hashedUserPassword, givenHash);
    }
    return false;
  }

  @JavascriptInterface
  public boolean createUser(String username, String email, String userPassword) {
    byte[] salt = Crypto.generateSecureByteArray(Constants.SALT_LENGTH);
    byte[] hashedUserPassword = Crypto.computeHash(userPassword, salt);
    User newUserEntity = new User(username, email, hashedUserPassword);

    try {
      userDataAccessObject.addUser(newUserEntity);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public boolean deleteUser(String username, String hashedUserPassword) {
    if (!checkUser(username, hashedUserPassword)) return false;

    User checkedUserEntity = new User(username);

    try {
      if (userDataAccessObject.deleteUser(checkedUserEntity) == 0)
        throw new RuntimeException("nothing was deleted");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @JavascriptInterface
  public String getSalt(String username) {
    User userEntity = userDataAccessObject.getUser(username);
    if (userEntity != null) {
      byte[] salt = Arrays.copyOf(userEntity.hashedUserPassword, Constants.SALT_LENGTH);
      return BaseEncoding.base16().encode(salt);
    }
    return "";
  }
}
