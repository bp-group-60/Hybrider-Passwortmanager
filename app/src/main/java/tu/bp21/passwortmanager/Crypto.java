package tu.bp21.passwortmanager;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.dao.PasswordDao;

public class Crypto {
  private static byte[] salt;
  private static byte[] key;
  private static String username;
  private static PasswordDao passwordDao;

  private static native byte[] crypt(byte[] input, byte[] aad, byte[] iv, byte[] key);

  private static native byte[] generateKey(
      byte[] input, int input_length, byte[] salt, int salt_length);

  public static native byte[] generateSecureByteArray(int size);

  private static native byte[] hash(byte[] input, int input_length, byte[] salt, int salt_length);

  public static void setPasswordDao(PasswordDao passwordDao) {
    Crypto.passwordDao = passwordDao;
  }

  public static void setGeneratedKey(String passwordToDerive) {
    byte[] input = passwordToDerive.getBytes();
    key = generateKey(input, input.length, salt, salt.length);
  }

  public static void setSalt(byte[] input) {
    salt = input;
  }

  public static byte[] encrypt(String plainText, String website) {
    byte[] input = plainText.getBytes();
    return crypt(input, (username + website).getBytes(), generateIV(), key);
  }

  public static String decrypt(byte[] cipher, String website) {
    byte[] text = crypt(cipher, (username + website).getBytes(), null, key);
    if (text == null) return "";
    return new String(text);
  }

  public static byte[] computeHash(String password) {
    byte[] input = password.getBytes();
    byte[] output = hash(input, input.length, salt, salt.length);
    if (output == null) throw new RuntimeException("Hash failed. Not enough RAM");
    return output;
  }

  public static void setCurrentUser(String username) {
    Crypto.username = username;
  }

  private static byte[] generateIV() {
    List<Password> list = passwordDao.getPasswordList(username);
    ArrayList<String> ivList = new ArrayList<>();
    int length = list.size();

    if (length >= Math.pow(2, 96))
      throw new RuntimeException("Reached maximum number of entries for this account");

    byte[] ivExisted;
    for (Password x : list) {
      ivExisted = Arrays.copyOf(x.password, 12);
      ivList.add(BaseEncoding.base16().encode(ivExisted));
    }

    byte[] ivNew = generateSecureByteArray(12);
    while (ivList.contains(BaseEncoding.base16().encode(ivNew)))
      ivNew = generateSecureByteArray(12);
    return ivNew;
  }
}
