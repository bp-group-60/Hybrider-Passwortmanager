package tu.bp21.passwortmanager.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User {

  @PrimaryKey @NonNull public String username;
  public String email;
  public byte[] password;

  public User(@NonNull String username, String email, byte[] password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  @Ignore
  public User(@NonNull String username) {
    this.username = username;
  }
}
