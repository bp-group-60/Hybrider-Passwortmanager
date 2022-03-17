package tu.bp21.passwortmanager.db.data_access_objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import tu.bp21.passwortmanager.db.entities.Password;

@Dao
public interface PasswordDataAccessObject {
  @Insert
  void addPassword(Password passwordEntity);

  @Delete
  int deletePassword(Password passwordEntity);

  @Query("SELECT * FROM Password WHERE username = :user")
  List<Password> getPasswordList(String user);

  @Query("SELECT * FROM Password WHERE username = :user AND websiteName = :website")
  Password getPassword(String user, String website);

  @Update
  int updatePassword(Password passwordEntity);
}
