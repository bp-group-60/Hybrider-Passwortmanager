package tu.bp21.passwortmanager.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
    primaryKeys = {"user", "website", "url"},
    foreignKeys = {
      @ForeignKey(
          entity = Password.class,
          parentColumns = {"user", "websiteName"},
          childColumns = {"user", "website"},
          onDelete = ForeignKey.CASCADE,
          onUpdate = ForeignKey.CASCADE)
    })
public class Website {
  @NonNull public String website;
  @NonNull public String user;

  @NonNull public String url;

  public Website(@NonNull String user, @NonNull String website, @NonNull String url) {
    this.user = user;
    this.website = website;
    this.url = url;
  }

  @Override
  public String toString() {
    return "\"" + url + "\"";
  }
}
