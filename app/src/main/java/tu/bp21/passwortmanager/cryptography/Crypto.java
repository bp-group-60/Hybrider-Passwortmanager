package tu.bp21.passwortmanager.cryptography;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;

import static tu.bp21.passwortmanager.Constants.*;

public class Crypto {
  private static native byte[] crypt(byte[] input, byte[] aad, byte[] iv, byte[] key, int tag_length, int iv_length);

  private static native byte[] generateKeyNative(
          byte[] input, int input_length, byte[] salt, int salt_length, int output_length, int iterations);

  public static native byte[] generateSecureByteArray(int size);

  private static native byte[] hash(byte[] input, int input_length, byte[] salt, int salt_length, int param_N, int param_r, int param_p, int output_length);

  public static byte[] generateKey(@NonNull String passwordToDerive, byte[] salt) {
    byte[] input = passwordToDerive.getBytes();
    if (salt == null) salt = new byte[0];
    return generateKeyNative(input, input.length, salt, salt.length, KEY_OUTPUT_LENGTH, PBKDF2_ITERATIONS);
  }

  public static byte[] encrypt(
      @NonNull String plainText, byte[] associatedData, @NonNull byte[] key, @NonNull byte[] iv) {
    byte[] input = plainText.getBytes();
    if (key.length != ENCRYPT_KEY_LENGTH || iv.length != ENCRYPT_IV_LENGTH) return new byte[0];
    if (associatedData == null) associatedData = new byte[0];
    return crypt(input, associatedData, iv, key, GCM_TAG_LENGTH, ENCRYPT_IV_LENGTH);
  }

  public static String decrypt(@NonNull byte[] cipher, byte[] associatedData, @NonNull byte[] key) {
    if (associatedData == null) associatedData = new byte[0];
    byte[] text = crypt(cipher, associatedData, null, key, GCM_TAG_LENGTH, ENCRYPT_IV_LENGTH);
    if (text == null) return "authentication failed";
    return new String(text);
  }

  public static byte[] computeHash(@NonNull String password, byte[] salt) {
    if (salt == null) salt = new byte[0];
    byte[] input = password.getBytes();
    byte[] output = hash(input, input.length, salt, salt.length, SCRYPT_PARAM_N, SCRYPT_PARAM_R, SCRYPT_PARAM_P, HASH_OUTPUT_LENGTH);
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
