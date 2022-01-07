package tu.bp21.passwortmanager.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        primaryKeys = {"user", "website", "url"},
        foreignKeys = {
                @ForeignKey(
                        entity = Password.class,
                        parentColumns = {"user", "websiteName"},
                        childColumns = {"user", "website"},
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        })
public class Website {
    @NonNull public String website;
    @NonNull public String user;

    @NonNull public String url;

    public Website(@NonNull String website, @NonNull String url, @NonNull String user){
        this.website = website;
        this.url = url;
        this.user = user;
    }
}