package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import tu.bp21.passwortmanager.db.dao.UserDao;
import tu.bp21.passwortmanager.db.User;

public class InterfaceUser {
  private final UserDao userDao;

  public InterfaceUser(UserDao userDao) {
    this.userDao = userDao;
  }

  @JavascriptInterface
  public boolean existUser(String user) {
    return userDao.getUser(user) != null;
  }

  @JavascriptInterface
  public boolean checkUser(String username, String hash) {
    User user = userDao.getUser(username);
    return user != null && user.password.equals(hash);
  }

  @JavascriptInterface
  public boolean createUser(String user, String email, String hash) {
    User newUser = new User(user, email, hash);

    try {
      userDao.addUser(newUser);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @JavascriptInterface
  public boolean deleteUser(String user, String hash) {
    User newUser = new User(user, hash);

    try {
      if (userDao.deleteUser(newUser) == 0) throw new RuntimeException("nothing was deleted");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
