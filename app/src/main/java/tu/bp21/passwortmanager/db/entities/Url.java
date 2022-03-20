package tu.bp21.passwortmanager.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
    primaryKeys = {"username", "websiteName", "webAddress"},
    foreignKeys = {
      @ForeignKey(
          entity = Website.class,
          parentColumns = {"username", "websiteName"},
          childColumns = {"username", "websiteName"},
          onDelete = ForeignKey.CASCADE,
          onUpdate = ForeignKey.CASCADE)
    })
public class Url {
  @NonNull public String websiteName;
  @NonNull public String username;

  @NonNull public String webAddress;

  public Url(@NonNull String username, @NonNull String websiteName, @NonNull String webAddress) {
    this.username = username;
    this.websiteName = websiteName;
    this.webAddress = webAddress;
  }

  // adapted for simple json delivery
  @Override
  public String toString() {
    return "\"" + webAddress + "\"";
  }
}
