package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.Arrays;

import tu.bp21.passwortmanager.Crypto;
import tu.bp21.passwortmanager.db.User;
import tu.bp21.passwortmanager.db.dao.UserDao;

public class InterfaceCrypto {
  private final UserDao userDataAccessObject;

  public InterfaceCrypto(UserDao userDataAccessObject) {
    this.userDataAccessObject = userDataAccessObject;
  }

  @JavascriptInterface
  public String hashPassword(String userPassword) {
    byte[] cipher = Crypto.computeHash(userPassword);
    return BaseEncoding.base16().encode(cipher);
  }

  @JavascriptInterface
  public void setSalt(String username) {
    User user = userDataAccessObject.getUser(username);
    if (user != null) Crypto.setSalt(Arrays.copyOf(user.password, 16));
  }

  @JavascriptInterface
  public void setGeneratedKey(String plainPassword) {
    Crypto.setGeneratedKey(plainPassword);
  }
}
