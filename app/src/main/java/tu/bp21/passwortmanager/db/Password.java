package tu.bp21.passwortmanager.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
    primaryKeys = {"user", "website"},
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
  @NonNull public String website;
  public String loginName;
  public String password;

  public Password(
      @NonNull String user, @NonNull String website, String loginName, String password) {
    this.user = user;
    this.website = website;
    this.loginName = loginName;
    this.password = password;
  }

  public String toSecureString() {
    return "[\"" + website + "\",\"" + loginName + "\"]";
  }

  @Override
  public String toString() {
    return "[\"" + website + "\",\"" + loginName + "\",\"" + password + "\"]";
  }
}
