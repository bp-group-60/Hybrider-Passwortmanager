package tu.bp21.passwortmanager.db.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tu.bp21.passwortmanager.db.data_access_objects.WebsiteDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.UserDataAccessObject;
import tu.bp21.passwortmanager.db.data_access_objects.UrlDataAccessObject;
import tu.bp21.passwortmanager.db.entities.Website;
import tu.bp21.passwortmanager.db.entities.User;
import tu.bp21.passwortmanager.db.entities.Url;

@Database(
    entities = {User.class, Website.class, Url.class},
    version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {
  public abstract UserDataAccessObject getUserDataAccessObject();

  public abstract WebsiteDataAccessObject getPasswordDataAccessObject();

  public abstract UrlDataAccessObject getWebsiteDataAccessObject();
}
