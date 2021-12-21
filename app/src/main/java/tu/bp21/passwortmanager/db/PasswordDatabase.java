package tu.bp21.passwortmanager.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
    entities = {User.class, Password.class},
    version = 1)
public abstract class PasswordDatabase extends RoomDatabase {
  public abstract PasswordDao getDao();
}
