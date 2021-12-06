package tu.bp21.passwortmanager.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"user", "website"},
        foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns = "username",
        childColumns = "user",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)}) // wird sehr warscheinlich nie vorkommen
public class Password {

    public Password(@NonNull String user, @NonNull String website, String loginName, String password){
        this.user = user;
        this.website = website;
        this.loginName = loginName;
        this.password = password;
    }

    @NonNull
    public String user;

    @NonNull
    public String website;

    public String loginName;

    public String password;
}
