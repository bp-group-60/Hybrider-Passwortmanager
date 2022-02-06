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
  public String hashPassword(String userPassword, String salt) {
    byte[] cipher = Crypto.computeHash(userPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(cipher);
  }

  @JavascriptInterface
  public String getSalt(String username) {
    User user = userDataAccessObject.getUser(username);
    if (user != null){
      byte[] salt = Arrays.copyOf(user.password, 16);
      return BaseEncoding.base16().encode(salt);
    }
    return "";
  }

  @JavascriptInterface
  public String generateKey(String plainPassword, String salt) {
    byte[] key = Crypto.generateKey(plainPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(key);
  }
}
