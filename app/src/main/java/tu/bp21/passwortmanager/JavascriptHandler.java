package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;
import tu.bp21.passwortmanager.DB.PasswortDao;
import tu.bp21.passwortmanager.DB.User;

public class JavascriptHandler {
    private PasswortDao passwortDao;

    public JavascriptHandler(PasswortDao dao){
        passwortDao = dao;
    }

    //Methoden für Anmeldung
    @JavascriptInterface
    public boolean existUser(String user){
        return passwortDao.getUser(user) != null;
    }

    @JavascriptInterface
    public boolean checkUser(String username, String hash){
        User user = passwortDao.getUser(username);
        return user != null && user.password.equals(hash);
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

    //Methoden für Datenbankabfragen
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