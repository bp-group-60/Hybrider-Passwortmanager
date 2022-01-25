package tu.bp21.passwortmanager.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import tu.bp21.passwortmanager.db.Password;

@Dao
public interface PasswordDao {
  @Insert
  void addPassword(Password password);

  @Delete
  int deletePassword(Password password);

  @Query("SELECT * FROM Password WHERE user = :user")
  List<Password> getPasswordList(String user);

  @Query("SELECT * FROM Password WHERE user = :user AND websiteName = :website")
  Password getPassword(String user, String website);

  @Update
  int updatePassword(Password password);
}