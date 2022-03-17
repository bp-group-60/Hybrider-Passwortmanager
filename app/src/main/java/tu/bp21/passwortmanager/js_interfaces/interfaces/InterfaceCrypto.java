package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import tu.bp21.passwortmanager.cryptography.Crypto;

public class InterfaceCrypto {

  @JavascriptInterface
  public String hashPassword(String plainUserPassword, String salt) {
    byte[] cipher = Crypto.computeHash(plainUserPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(cipher);
  }

  @JavascriptInterface
  public String generateKey(String plainUserPassword, String salt) {
    byte[] key = Crypto.generateKey(plainUserPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(key);
  }
}
