package tu.bp21.passwortmanager.DB;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    //Für Entität User
    @Insert
    void addUser(User user);

    @Delete
    int deleteUser(User user);

    @Query("SELECT uid FROM user WHERE password = :password AND (username = :loginName OR email = :loginName)")
    int getUID(String password, String loginName);

    @Query("SELECT * FROM User WHERE uid = :uid")
    User getUser(int uid);

    //Für Entität Password
    @Insert
    void addPassword(Password password);

    @Delete
    int deletePassword(Password password);

    @Query("SELECT website, loginName, password FROM Password WHERE uid = :uid")
    List<Password> getPasswordList(int uid);

    @Query("SELECT * FROM Password WHERE uid = :uid AND website = :website")
    Password getPassword(int uid, String website);

    @Update
    int updatePassword(Password password);

}
