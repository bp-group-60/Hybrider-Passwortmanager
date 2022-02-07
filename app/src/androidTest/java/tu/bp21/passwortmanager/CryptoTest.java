package tu.bp21.passwortmanager;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import org.bouncycastle.crypto.generators.SCrypt;

import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class CryptoTest {

  static {
    System.loadLibrary("Crypto");
  }

  static Random random;
  static int ivSize, keySize, hashSize;

  @BeforeAll
  static void setUp() {
    random = new Random();
    ivSize = 12;
    keySize = 32;
    hashSize = 64;
  }

  @Test
  void generateSecureByteArrayTest() {
    int size = new Random().nextInt(64) + 1;
    byte[] array = Crypto.generateSecureByteArray(size);
    assertEquals(size, array.length);
    for (byte b : array) {
      assertNotNull(b);
    }
    array = Crypto.generateSecureByteArray(0);
    assertEquals(0, array.length);
  }

  @Nested
  @DisplayName("Tests for encrypt")
  class encryptTest {

    @Test
    @DisplayName("Case: Default")
    void encryptDefault() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      random.nextBytes(associatedData);
      String plaintext = generateRandomString(40);
      checkEncryptExpected(associatedData, plaintext);
    }

    @Test
    @DisplayName("Case: empty or null associatedData")
    void encryptEmptyOrNullAssociatedData() throws Exception {
      byte[] associatedData = new byte[0];
      String plaintext = generateRandomString(40);
      checkEncryptExpected(associatedData, plaintext);
      checkEncryptExpected(null, plaintext);
    }

    @Test
    @DisplayName("Case: empty plaintext")
    void encryptEmptyPlaintext() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      random.nextBytes(associatedData);
      String plaintext = "";
      checkEncryptExpected(associatedData, plaintext);
    }

    @Test
    @DisplayName("Case: null plaintext")
    void encryptNullPlainText() {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      byte[] key = new byte[keySize];
      byte[] iv = new byte[ivSize];
      random.nextBytes(associatedData);
      random.nextBytes(key);
      random.nextBytes(iv);
      assertThrows(NullPointerException.class, () -> Crypto.encrypt(null, associatedData, key, iv));
    }

    @Test
    @DisplayName("Case: illegal Key Size")
    void encryptIllegalKeySize() {
      int illegalSize = random.nextInt(100);
      while (illegalSize == keySize) illegalSize = random.nextInt(100);
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      byte[] key = new byte[illegalSize];
      byte[] iv = new byte[ivSize];
      String plaintext = generateRandomString(40);
      random.nextBytes(associatedData);
      random.nextBytes(key);
      random.nextBytes(iv);
      byte[] expected = new byte[0];
      byte[] actual = Crypto.encrypt(plaintext, associatedData, key, iv);
      assertArrayEquals(expected, actual);
      assertEquals(0, actual.length);
    }

    @Test
    @DisplayName("Case: illegal IV Size")
    void encryptIllegalIVSize() {
      int illegalSize = random.nextInt(100);
      while (illegalSize == ivSize) illegalSize = random.nextInt(100);
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      byte[] key = new byte[keySize];
      byte[] iv = new byte[illegalSize];
      String plaintext = generateRandomString(40);
      random.nextBytes(associatedData);
      random.nextBytes(key);
      random.nextBytes(iv);
      byte[] expected = new byte[0];
      byte[] actual = Crypto.encrypt(plaintext, associatedData, key, iv);
      assertArrayEquals(expected, actual);
      assertEquals(0, actual.length);
    }

    /** compute the expected and actual encryption value, then compare both */
    void checkEncryptExpected(byte[] associatedData, String plaintext) throws Exception {
      byte[] aadForExpected = associatedData;
      if (associatedData == null) aadForExpected = new byte[0];
      SecretKey key = generateKey();
      byte[] keyAsByte = key.getEncoded();
      byte[] expected = encryptWithGCM(plaintext, aadForExpected, key);

      byte[] iv = Arrays.copyOf(expected, ivSize);
      byte[] actual = Crypto.encrypt(plaintext, associatedData, keyAsByte, iv);

      assertArrayEquals(expected, actual);
    }
  }

  @Nested
  @DisplayName("Tests for decrypt")
  class decryptTest {

    @Test
    @DisplayName("Case: Default")
    void decryptDefault() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      random.nextBytes(associatedData);
      String expected = generateRandomString(40);
      checkDecryptSuccess(associatedData, expected);
    }

    @Test
    @DisplayName("Case: Empty or Null associatedData")
    void decryptEmptyOrNullAssociatedData() throws Exception {
      byte[] associatedData = new byte[0];
      String expected = generateRandomString(40);
      checkDecryptSuccess(associatedData, expected);
      checkDecryptSuccess(null, expected);
    }

    @Test
    @DisplayName("Case: Empty plaintext")
    void decryptEmptyPlaintext() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      random.nextBytes(associatedData);
      String expected = "";
      checkDecryptSuccess(associatedData, expected);
    }

    @Test
    @DisplayName("Case: Wrong Key")
    void decryptWrongKey() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      byte[] actualKey = new byte[keySize];
      random.nextBytes(associatedData);
      random.nextBytes(actualKey);
      String expected = generateRandomString(40);
      SecretKey key = generateKey();
      byte[] expectedKey = key.getEncoded();

      while (Arrays.areEqual(expectedKey, actualKey)) random.nextBytes(actualKey);

      byte[] cipher = encryptWithGCM(expected, associatedData, key);
      String actual = Crypto.decrypt(cipher, associatedData, actualKey);
      assertFalse(expected.equals(actual));
      assertTrue(actual.equals("authentication failed"));
    }

    @Test
    @DisplayName("Case: Wrong associatedData")
    void decryptWrongAssociatedData() throws Exception {
      byte[] associatedData1 = new byte[random.nextInt(100) + 1];
      byte[] associatedData2 = new byte[random.nextInt(100) + 1];
      random.nextBytes(associatedData1);
      random.nextBytes(associatedData2);
      while (Arrays.areEqual(associatedData1, associatedData2)) random.nextBytes(associatedData2);

      String expected = generateRandomString(40);
      SecretKey key = generateKey();
      byte[] hexKey = key.getEncoded();

      byte[] cipher = encryptWithGCM(expected, associatedData1, key);
      String actual = Crypto.decrypt(cipher, associatedData2, hexKey);
      assertFalse(expected.equals(actual));
      assertTrue(actual.equals("authentication failed"));
    }

    @Test
    @DisplayName("Case: cipher was modified")
    void decryptModifiedCipher() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100) + 1];
      random.nextBytes(associatedData);
      String expected = generateRandomString(40);
      SecretKey key = generateKey();
      byte[] hexKey = key.getEncoded();

      byte[] cipher = encryptWithGCM(expected, associatedData, key);
      int position = random.nextInt(cipher.length);
      byte oldByte = cipher[position];
      cipher[position] = (byte) (random.nextInt(256) - 128);
      while (oldByte == cipher[position]) cipher[position] = (byte) (random.nextInt(256) - 128);

      String actual = Crypto.decrypt(cipher, associatedData, hexKey);
      assertFalse(expected.equals(actual));
      assertTrue(actual.equals("authentication failed"));
    }

    void checkDecryptSuccess(byte[] associatedData, String expected) throws Exception {
      byte[] aadtoEncrypt = associatedData;
      if (associatedData == null) aadtoEncrypt = new byte[0];
      SecretKey key = generateKey();
      byte[] hexKey = key.getEncoded();
      byte[] cipher = encryptWithGCM(expected, aadtoEncrypt, key);
      String actual = Crypto.decrypt(cipher, associatedData, hexKey);
      assertTrue(expected.equals(actual));
    }
  }

  @Nested
  @DisplayName("Tests for computeHash")
  class computeHashTest {
    @Test
    @DisplayName("Case: Default")
    void computeHashDefault() {
      byte[] salt = Crypto.generateSecureByteArray(random.nextInt(64) + 1);
      String password = generateRandomString(40);
      byte[] expected = computeExpectedHash(password, salt, hashSize);
      byte[] actual = Crypto.computeHash(password, salt);

      assertArrayEquals(expected, actual);
      assertEquals(hashSize + salt.length, actual.length);
    }

    @Test
    @DisplayName("Case: no salt")
    void computeHashNoSalt() {
      byte[] salt = new byte[0];
      String password = generateRandomString(40);
      byte[] expected = computeExpectedHash(password, salt, hashSize);
      byte[] actual = Crypto.computeHash(password, salt);

      assertArrayEquals(expected, actual);
      assertEquals(hashSize, actual.length);

      actual = Crypto.computeHash(password, null);
      assertArrayEquals(expected, actual);
      assertEquals(hashSize, actual.length);
    }

    @Test
    @DisplayName("Case: empty or null plaintext")
    void computeHashEmpty() {
      byte[] salt = Crypto.generateSecureByteArray(random.nextInt(64) + 1);
      String password = "";
      byte[] expected = computeExpectedHash(password, salt, hashSize);
      byte[] actual = Crypto.computeHash(password, salt);

      assertArrayEquals(expected, actual);
      assertEquals(hashSize + salt.length, actual.length);

      assertThrows(NullPointerException.class, () -> Crypto.computeHash(null, salt));
    }

    /** compute scrypt hash with bouncy castle library */
    byte[] computeExpectedHash(String plaintext, byte[] salt, int outputLength) {
      byte[] expected =
          SCrypt.generate(plaintext.getBytes(), salt, (int) Math.pow(2, 18), 8, 1, outputLength);
      expected = Arrays.concatenate(salt, expected);
      return expected;
    }
  }

  /** generate a key for encrypt and decrypt */
  SecretKey generateKey() throws Exception {
    KeyGenerator keygen = KeyGenerator.getInstance("AES");
    keygen.init(256);
    return keygen.generateKey();
  }

  /** encrypt with GCM mode using android library */
  byte[] encryptWithGCM(String plaintext, byte[] aad, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    cipher.updateAAD(aad);
    byte[] iv = cipher.getIV();
    assertEquals(ivSize, iv.length);
    byte[] output = cipher.doFinal(plaintext.getBytes());
    output = Arrays.concatenate(iv, output);
    return output;
  }
}
