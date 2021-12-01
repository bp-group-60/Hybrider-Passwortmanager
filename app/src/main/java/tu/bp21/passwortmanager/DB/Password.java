package tu.bp21.passwortmanager.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"uid", "website"},
        foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns = "uid",
        childColumns = "uid",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)})
public class Password {
    
    public int uid;

    @NonNull
    public String website;

    public String loginName;

    public String password;
}
