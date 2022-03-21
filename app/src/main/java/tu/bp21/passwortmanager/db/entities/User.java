package tu.bp21.passwortmanager.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User {

  @PrimaryKey @NonNull public String username;
  public String email;
  public byte[] hashedUserPassword;

  public User(@NonNull String username, String email, byte[] hashedUserPassword) {
    this.username = username;
    this.email = email;
    this.hashedUserPassword = hashedUserPassword;
  }

  @Ignore
  public User(@NonNull String username) {
    this.username = username;
  }
}
