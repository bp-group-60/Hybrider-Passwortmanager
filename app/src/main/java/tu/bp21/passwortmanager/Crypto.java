package tu.bp21.passwortmanager;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;

public class Crypto {
  private static native byte[] crypt(byte[] input, byte[] aad, byte[] iv, byte[] key);

  private static native byte[] generateKeyNative(
      byte[] input, int input_length, byte[] salt, int salt_length);

  public static native byte[] generateSecureByteArray(int size);

  private static native byte[] hash(byte[] input, int input_length, byte[] salt, int salt_length);

  public static byte[] generateKey(@NonNull String passwordToDerive, byte[] salt) {
    byte[] input = passwordToDerive.getBytes();
    if(salt==null) salt = new byte[0];
    return generateKeyNative(input, input.length, salt, salt.length);
  }

  public static byte[] encrypt(
      @NonNull String plainText, byte[] associatedData, @NonNull byte[] key, @NonNull byte[] iv) {
    byte[] input = plainText.getBytes();
    if (key.length != 32 || iv.length != 12) return new byte[0];
    if (associatedData == null) associatedData = new byte[0];
    return crypt(input, associatedData, iv, key);
  }

  public static String decrypt(@NonNull byte[] cipher, byte[] associatedData, @NonNull byte[] key) {
    if (associatedData == null) associatedData = new byte[0];
    byte[] text = crypt(cipher, associatedData, null, key);
    if (text == null) return "authentication failed";
    return new String(text);
  }

  public static byte[] computeHash(@NonNull String password, byte[] salt) {
    if (salt == null) salt = new byte[0];
    byte[] input = password.getBytes();
    byte[] output = hash(input, input.length, salt, salt.length);
    if (output == null) throw new RuntimeException("Hash failed. Not enough RAM");
    return output;
  }

  public static byte[] generateUniqueIV(ArrayList<String> ivList, int size) {
    if (ivList != null && ivList.size() >= Math.pow(2, size * 8))
      throw new RuntimeException("reached maximum amount of entries");

    byte[] ivNew = generateSecureByteArray(size);
    if (ivList == null || ivList.size() == 0) return ivNew;
    while (ivList.contains(BaseEncoding.base16().encode(ivNew)))
      ivNew = generateSecureByteArray(size);
    return ivNew;
  }
}
