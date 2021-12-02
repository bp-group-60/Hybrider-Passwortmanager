package tu.bp21.passwortmanager;

import android.webkit.JavascriptInterface;

public class JavascriptHandler {

    //methoden für Anmeldung
    @JavascriptInterface
    public boolean existUser(String user){
        return true;
    }

    @JavascriptInterface
    public boolean checkUser(String user, String hash){
        return true;
    }

    @JavascriptInterface
    public boolean createUser(String user, String hash){
        return true;
    }

    //methoden für Datenbankabfragen
    @JavascriptInterface
    public String getPasswds(){
        return "<p>Hello World!<p/>";
    }
}
