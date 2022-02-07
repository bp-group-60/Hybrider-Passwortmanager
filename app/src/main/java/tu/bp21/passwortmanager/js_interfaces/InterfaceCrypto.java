package tu.bp21.passwortmanager.js_interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;


import tu.bp21.passwortmanager.Crypto;

public class InterfaceCrypto {

  @JavascriptInterface
  public String hashPassword(String userPassword, String salt) {
    byte[] cipher = Crypto.computeHash(userPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(cipher);
  }

  @JavascriptInterface
  public String generateKey(String plainPassword, String salt) {
    byte[] key = Crypto.generateKey(plainPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(key);
  }
}
