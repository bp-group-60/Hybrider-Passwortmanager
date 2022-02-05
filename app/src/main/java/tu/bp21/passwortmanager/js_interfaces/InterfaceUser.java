package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.Arrays;

import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.db.dao.UserDao;
import tu.bp21.passwortmanager.db.User;

public class InterfaceUser {
  private final UserDao userDataAccessObject;

  public InterfaceUser(UserDao userDataAccessObject) {
    this.userDataAccessObject = userDataAccessObject;
  }

  @JavascriptInterface
  public boolean existUser(String user) {
    return userDataAccessObject.getUser(user) != null;
  }

  @JavascriptInterface
  public boolean checkUser(String username, String userPassword) {
    User user = userDataAccessObject.getUser(username);
    if(user != null) {
      byte[] cipher = BaseEncoding.base16().decode(userPassword.toUpperCase());
      if(Arrays.equals(user.password,cipher)) {
        return true;
      }
    }
    return false;
  }

  @JavascriptInterface
  public boolean createUser(String username, String email, String userPassword) {
    Crypto.setSalt(Crypto.generateSecureByteArray(16));
    byte[] hashValue = Crypto.computeHash(userPassword);
    User newUser = new User(username, email, hashValue);

    try {
      userDataAccessObject.addUser(newUser);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deleteUser(String username, String userPassword) {
    if(!checkUser(username, userPassword))
      return false;

    User newUser = new User(username);

    try {
      if (userDataAccessObject.deleteUser(newUser) == 0) throw new RuntimeException("nothing was deleted");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
