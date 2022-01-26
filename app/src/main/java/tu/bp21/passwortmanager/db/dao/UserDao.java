package tu.bp21.passwortmanager.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import tu.bp21.passwortmanager.db.User;

@Dao
public interface UserDao {
    @Insert
    void addUser(User user);

    @Delete
    int deleteUser(User user);

    @Query("SELECT * FROM User WHERE username = :user")
    User getUser(String user);
}
