package tu.bp21.passwortmanager.db.data_access_objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import tu.bp21.passwortmanager.db.entities.User;

@Dao
public interface UserDataAccessObject {
  @Insert
  void addUser(User userEntity);

  @Delete
  int deleteUser(User userEntity);

  @Query("SELECT * FROM User WHERE username = :userName")
  User getUser(String userName);
}
