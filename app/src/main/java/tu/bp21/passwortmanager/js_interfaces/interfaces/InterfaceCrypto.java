package tu.bp21.passwortmanager.js_interfaces.interfaces;

import android.webkit.JavascriptInterface;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import tu.bp21.passwortmanager.cryptography.Crypto;

public class InterfaceCrypto {

  @JavascriptInterface
  public String hashUserPassword(String userPassword, String salt) {
    byte[] hashedPassword = Crypto.computeHash(userPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(hashedPassword);
  }

  @JavascriptInterface
  public String generateEncryptionKey(String userPassword, String salt) {
    byte[] encryptionKey = Crypto.generateKey(userPassword, BaseEncoding.base16().decode(salt));
    return BaseEncoding.base16().encode(encryptionKey);
  }
}
