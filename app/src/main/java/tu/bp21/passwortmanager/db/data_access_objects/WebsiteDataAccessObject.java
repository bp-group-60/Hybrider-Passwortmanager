package tu.bp21.passwortmanager.db.data_access_objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import tu.bp21.passwortmanager.db.entities.Website;

@Dao
public interface WebsiteDataAccessObject {
  @Insert
  void addWebsite(Website websiteEntity);

  @Delete
  int deleteWebsite(Website websiteEntity);

  @Query("SELECT * FROM Website WHERE username = :username")
  List<Website> getWebsiteList(String username);

  @Query("SELECT * FROM Website WHERE username = :username AND websiteName = :websiteName")
  Website getWebsite(String username, String websiteName);

  @Update
  int updateWebsite(Website websiteEntity);
}
