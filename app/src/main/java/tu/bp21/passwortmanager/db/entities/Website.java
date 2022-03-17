package tu.bp21.passwortmanager.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
    primaryKeys = {"username", "websiteName", "url"},
    foreignKeys = {
      @ForeignKey(
          entity = Password.class,
          parentColumns = {"username", "websiteName"},
          childColumns = {"username", "websiteName"},
          onDelete = ForeignKey.CASCADE,
          onUpdate = ForeignKey.CASCADE)
    })
public class Website {
  @NonNull public String websiteName;
  @NonNull public String username;

  @NonNull public String url;

  public Website(@NonNull String username, @NonNull String websiteName, @NonNull String url) {
    this.username = username;
    this.websiteName = websiteName;
    this.url = url;
  }

  @Override
  public String toString() {
    return "\"" + url + "\"";
  }
}
