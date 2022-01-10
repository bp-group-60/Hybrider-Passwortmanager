package tu.bp21.passwortmanager.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

  @PrimaryKey @NonNull public String username;
  public String email;
  public String password;

  public User(@NonNull String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  @NonNull
  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
