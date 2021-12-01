package tu.bp21.passwortmanager.DB;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns = "uid",
        childColumns = "uid",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)})
public class Password {

    @PrimaryKey
    public int uid;

    @PrimaryKey
    public String website;

    public String loginName;

    public String password;

}
