package tu.bp21.passwortmanager.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(
    primaryKeys = {"username", "websiteName"},
    foreignKeys = {
      @ForeignKey(
          entity = User.class,
          parentColumns = "username",
          childColumns = "username",
          onDelete = ForeignKey.CASCADE,
          onUpdate = ForeignKey.CASCADE)
    })
public class Website {
  @NonNull public String username;
  @NonNull public String websiteName;
  public String loginName;
  public byte[] encryptedLoginPassword;

  public Website(
      @NonNull String username, @NonNull String websiteName, String loginName, byte[] encryptedLoginPassword) {
    this.username = username;
    this.websiteName = websiteName;
    this.loginName = loginName;
    this.encryptedLoginPassword = encryptedLoginPassword;
  }

  @Ignore
  public Website(@NonNull String username, @NonNull String websiteName) {
    this.username = username;
    this.websiteName = websiteName;
  }

  // adapted for simple json delivery
  @Override
  public String toString() {
    return "[\"" + websiteName + "\",\"" + loginName + "\"]";
  }
}
