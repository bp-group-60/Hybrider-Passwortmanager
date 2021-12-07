package tu.bp21.passwortmanager.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    public User(@NonNull String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @PrimaryKey
    @NonNull
    public String username;

    public String email;

    public String password;
}
