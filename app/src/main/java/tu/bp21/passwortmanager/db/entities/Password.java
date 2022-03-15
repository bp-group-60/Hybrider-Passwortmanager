package tu.bp21.passwortmanager.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(
    primaryKeys = {"user", "websiteName"},
    foreignKeys = {
      @ForeignKey(
          entity = User.class,
          parentColumns = "username",
          childColumns = "user",
          onDelete = ForeignKey.CASCADE,
          onUpdate = ForeignKey.CASCADE)
    })
public class Password {

  @NonNull public String user;
  @NonNull public String websiteName;
  public String loginName;
  public byte[] password;

  public Password(
      @NonNull String user, @NonNull String websiteName, String loginName, byte[] password) {
    this.user = user;
    this.websiteName = websiteName;
    this.loginName = loginName;
    this.password = password;
  }

  @Ignore
  public Password(@NonNull String username, @NonNull String websiteName) {
    this.user = username;
    this.websiteName = websiteName;
  }

  @Override
  public String toString() {
    return "[\"" + websiteName + "\",\"" + loginName + "\"]";
  }
}
