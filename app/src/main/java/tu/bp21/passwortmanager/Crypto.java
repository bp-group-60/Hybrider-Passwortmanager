package tu.bp21.passwortmanager;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;

import tu.bp21.passwortmanager.db.dao.PasswordDao;

public class Crypto {
  private static PasswordDao passwordDao;

  private static native byte[] crypt(byte[] input, byte[] aad, byte[] iv, byte[] key);

  private static native byte[] generateKeyNative(
      byte[] input, int input_length, byte[] salt, int salt_length);

  public static native byte[] generateSecureByteArray(int size);

  private static native byte[] hash(byte[] input, int input_length, byte[] salt, int salt_length);

  public static void setPasswordDao(PasswordDao passwordDao) {
    Crypto.passwordDao = passwordDao;
  }

  public static byte[] generateKey(String passwordToDerive, byte[] salt) {
    byte[] input = passwordToDerive.getBytes();
    return generateKeyNative(input, input.length, salt, salt.length);
  }

  public static byte[] encrypt(String username, String website, String plainText, byte[] key, byte[] iv) {
    byte[] input = plainText.getBytes();
    return crypt(input, (username + website).getBytes(), iv, key);
  }

  public static String decrypt(String username, String website, byte[] cipher, byte[] key) {
    byte[] text = crypt(cipher, (username + website).getBytes(), null, key);
    if (text == null) return "authentication failed";
    return new String(text);
  }

  public static byte[] computeHash(String password, byte[] salt) {
    byte[] input = password.getBytes();
    byte[] output = hash(input, input.length, salt, salt.length);
    if (output == null) throw new RuntimeException("Hash failed. Not enough RAM");
    return output;
  }

  public static byte[] generateIV(ArrayList<String> ivList, int size) {

    byte[] ivNew = generateSecureByteArray(size);
    if(ivList == null || ivList.size() == 0) return ivNew;
    while (ivList.contains(BaseEncoding.base16().encode(ivNew)))
      ivNew = generateSecureByteArray(size);
    return ivNew;
  }
}
