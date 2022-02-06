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

  public static byte[] encrypt(String username, String website, String plainText) {
    byte[] input = plainText.getBytes();
    return crypt(input, (username + website).getBytes(), generateIV(username,12), key);
  }

  public static String decrypt(String username, String website, byte[] cipher) {
    byte[] text = crypt(cipher, (username + website).getBytes(), null, key);
    if (text == null) return "authentication failed";
    return new String(text);
  }

  public static byte[] computeHash(String password) {
    byte[] input = password.getBytes();
    byte[] output = hash(input, input.length, salt, salt.length);
    if (output == null) throw new RuntimeException("Hash failed. Not enough RAM");
    return output;
  }

  private static byte[] generateIV(String username, int size) {
    List<Password> list = passwordDao.getPasswordList(username);
    ArrayList<String> ivList = new ArrayList<>();
    int length = list.size();

    if (length >= Math.pow(2, size*8))
      throw new RuntimeException("Reached maximum number of entries for this account");

    byte[] ivExisted;
    for (Password x : list) {
      ivExisted = Arrays.copyOf(x.password, size);
      ivList.add(BaseEncoding.base16().encode(ivExisted));
    }

    byte[] ivNew = generateSecureByteArray(size);
    while (ivList.contains(BaseEncoding.base16().encode(ivNew)))
      ivNew = generateSecureByteArray(size);
    return ivNew;
  }
}
