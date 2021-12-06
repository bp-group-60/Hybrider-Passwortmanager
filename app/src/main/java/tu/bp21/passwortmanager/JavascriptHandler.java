package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;
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
        if(passwortDao.getUser(user) == null)
            return false;
        return true;
    }

    @JavascriptInterface
    public boolean checkUser(String username, String hash){
        User user = passwortDao.getUser(username);

        if(user != null && user.password == hash)
            return true;
        return false;
    }

    @JavascriptInterface
    public boolean createUser(String user, String email, String hash){
        User newUser = new User(user, email, hash);

        try {
            passwortDao.addUser(newUser);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
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