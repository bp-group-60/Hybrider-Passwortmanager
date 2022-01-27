package tu.bp21.passwortmanager.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tu.bp21.passwortmanager.db.dao.PasswordDao;
import tu.bp21.passwortmanager.db.dao.UserDao;
import tu.bp21.passwortmanager.db.dao.WebsiteDao;

@Database(
    entities = {User.class, Password.class, Website.class},
    version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
  public abstract UserDao getUserDao();

  public abstract PasswordDao getPasswordDao();

  public abstract WebsiteDao getWebsiteDao();
}
