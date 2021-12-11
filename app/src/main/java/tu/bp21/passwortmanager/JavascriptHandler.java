package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;
import java.util.ArrayList;
import tu.bp21.passwortmanager.db.PasswortDao;
import tu.bp21.passwortmanager.db.User;

/** Framework that can be used in JavaScript. */
public class JavascriptHandler {
  private final PasswortDao passwortDao;

  public JavascriptHandler(PasswortDao dao) {
    passwortDao = dao;
  }

  @JavascriptInterface
  public boolean existUser(String user) {
    return passwortDao.getUser(user) != null;
  }

  @JavascriptInterface
  public boolean checkUser(String username, String hash) {
    User user = passwortDao.getUser(username);
    return user != null && user.password.equals(hash);
  }

  @JavascriptInterface
  public boolean createUser(String user, String email, String hash) {
    User newUser = new User(user, email, hash);

    try {
      passwortDao.addUser(newUser);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public String getPasswordList(String user, String hash) {
    ArrayList<String> list = new ArrayList<>();
    passwortDao.getPasswordList(user).forEach(x -> list.add(x.toString()));
    return "{\"overview\":" + list.toString() + "}";
  }
}
