package tu.bp21.passwortmanager;

import android.content.Context;
import android.webkit.JavascriptInterface;

import androidx.room.Room;

import tu.bp21.passwortmanager.DB.PasswordDatabase;
import tu.bp21.passwortmanager.DB.PasswortDao;
import tu.bp21.passwortmanager.DB.User;

public class JavascriptHandler {
    PasswortDao passwortDao;

    public JavascriptHandler(PasswortDao dao){
        passwortDao = dao;
    }

    //methoden für Anmeldung
    @JavascriptInterface
    public boolean existUser(String user){

        return true;
    }

    @JavascriptInterface
    public boolean checkUser(String username, String hash){
        User user = passwortDao.getUser(username);
        if(user.password != hash)
            return false;
        return true;
    }

    @JavascriptInterface
    public boolean createUser(String user, String email, String hash){
        User newUser = new User(user, email, hash);
        boolean userAdded;

        try {
            userAdded = passwortDao.addUser(newUser);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return userAdded;
    }

    //methoden für Datenbankabfragen
    @JavascriptInterface
    public String getPasswordList(String user){
        String data = "admin";
        String output = "{\"length\":," + 1 +
                "\"websites\":[" + data + "]," +
                "\"loginnames\":[" + data + "]," +
                "\"passwords\":[" + data + "]}";

        return output;
    }
}