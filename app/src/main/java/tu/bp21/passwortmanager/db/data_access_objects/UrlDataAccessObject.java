package tu.bp21.passwortmanager.db.data_access_objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tu.bp21.passwortmanager.db.entities.Url;

@Dao
public interface UrlDataAccessObject {
  @Insert
  void addUrl(Url urlEntity);

  @Delete
  int deleteUrl(Url urlEntity);

  @Query("SELECT * FROM Url WHERE username = :username AND websiteName = :websiteName")
  List<Url> getUrlList(String username, String websiteName);
}
