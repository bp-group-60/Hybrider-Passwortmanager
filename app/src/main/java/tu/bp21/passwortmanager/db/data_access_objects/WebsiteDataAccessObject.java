package tu.bp21.passwortmanager.db.data_access_objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tu.bp21.passwortmanager.db.entities.Website;

@Dao
public interface WebsiteDataAccessObject {
  @Insert
  void addWebsite(Website websiteEntity);

  @Delete
  int deleteWebsite(Website websiteEntity);

  @Query("SELECT * FROM Website WHERE username = :user AND websiteName = :website")
  List<Website> getWebsiteList(String user, String website);
}
