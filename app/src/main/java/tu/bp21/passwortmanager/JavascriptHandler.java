package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;

public class JavascriptHandler {

    //Methoden für Anmeldung
    @JavascriptInterface
    public boolean existUser(String user){
        return true;
    }

    @JavascriptInterface
    public boolean checkUserPasswd(String user, String hash){
        return true;
    }

    @JavascriptInterface
    public boolean createUserPasswd(String user, String hash){
        return true;
    }

    //Methoden für Datenbankabfragen
    @JavascriptInterface
    public String getPasswds(){
        return "<p>Hello World!<p/>";
    }
}