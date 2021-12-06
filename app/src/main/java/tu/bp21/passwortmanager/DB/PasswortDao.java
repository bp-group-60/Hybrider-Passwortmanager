package tu.bp21.passwortmanager.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PasswortDao {
    //Für Entität User
    @Insert
    boolean addUser(User user);

    @Delete
    int deleteUser(User user);

    @Query("SELECT * FROM User WHERE username = :user")
    User getUser(String user);

    //Für Entität Password
    @Insert
    void addPassword(Password password);

    @Delete
    int deletePassword(Password password);

    @Query("SELECT * FROM Password WHERE user = :user")
    List<Password> getPasswordList(String user);

    @Query("SELECT * FROM Password WHERE user = :user AND website = :website")
    Password getPassword(String user, String website);

    @Update
    int updatePassword(Password password);

}
