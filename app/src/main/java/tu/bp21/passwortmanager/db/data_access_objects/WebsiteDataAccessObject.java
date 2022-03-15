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
  void addWebsite(Website website);

  @Delete
  int deleteWebsite(Website website);

  @Query("SELECT * FROM Website WHERE user = :user AND website = :website")
  List<Website> getWebsiteList(String user, String website);
}
