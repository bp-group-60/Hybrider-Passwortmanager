package tu.bp21.passwortmanager.db.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tu.bp21.passwortmanager.db.data_access_objects.PasswordDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.UserDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Password;
import tu.bp21.passwortmanager.db.entities.User;
import tu.bp21.passwortmanager.db.entities.Website;

@Database(
    entities = {User.class, Password.class, Website.class},
    version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
  public abstract UserDataAccessObject getUserDao();

  public abstract PasswordDataAccessObject getPasswordDao();

  public abstract WebsiteDataAccessObject getWebsiteDao();
}
