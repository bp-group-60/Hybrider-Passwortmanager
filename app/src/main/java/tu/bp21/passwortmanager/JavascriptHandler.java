package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.PasswordDao;
import tu.bp21.passwortmanager.db.User;

/** Framework that can be used in JavaScript. */
public class JavascriptHandler {
  private final PasswordDao passwordDao;

  public JavascriptHandler(PasswordDao dao) {
    passwordDao = dao;
  }

  @JavascriptInterface
  public boolean existUser(String user) {
    return passwordDao.getUser(user) != null;
  }

  @JavascriptInterface
  public boolean checkUser(String username, String hash) {
    User user = passwordDao.getUser(username);
    return user != null && user.password.equals(hash);
  }

  @JavascriptInterface
  public boolean createUser(String user, String email, String hash) {
    User newUser = new User(user, email, hash);

    try {
      passwordDao.addUser(newUser);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean createPassword(String user, String website, String loginName, String password) {
    Password newPassword = new Password(user, website, loginName, Crypto.encrypt(password));

    try {
      passwordDao.addPassword(newPassword);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }



  @JavascriptInterface
  public String getPasswordList(String user, String hash) {
    ArrayList<String> list = new ArrayList<>();
    passwordDao.getPasswordList(user).forEach(x -> {
      //x.loginName = new String(new Crypto().crypt(x.password, new String("abababababababababababababababab").getBytes(StandardCharsets.UTF_8), false));
      list.add(x.toString());
    });
    return "{\"dataArray\":" + list.toString() + "}";
  }
}
