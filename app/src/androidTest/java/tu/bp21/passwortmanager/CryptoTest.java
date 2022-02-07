package tu.bp21.passwortmanager;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import org.bouncycastle.crypto.generators.SCrypt;

import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.AfterEach;
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

  @BeforeAll
  static void setUp() {
    random = new Random();
  }

  @AfterEach
  void tearDown() {}

  @Test
  void generateSecureByteArrayTest() {
    int saltSize = new Random().nextInt(64) + 1;
    byte[] salt = Crypto.generateSecureByteArray(saltSize);
    assertEquals(saltSize, salt.length);
    for (byte b : salt) {
      assertNotNull(b);
    }
    salt = Crypto.generateSecureByteArray(0);
    assertEquals(0, salt.length);
  }

  @Test
  void encrypt() throws Exception {
    byte[] associatedData = new byte[random.nextInt(100) + 1];
    random.nextBytes(associatedData);
    String plaintext = generateRandomString(40);

    KeyGenerator keygen = KeyGenerator.getInstance("AES");
    keygen.init(256);
    SecretKey key = keygen.generateKey();
    byte[] keyAsByte = key.getEncoded();

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    cipher.updateAAD(associatedData);
    byte[] iv = cipher.getIV();
    assertEquals(12, iv.length);
    byte[] expected = cipher.doFinal(plaintext.getBytes());
    expected = Arrays.concatenate(iv, expected);
    byte[] actual = Crypto.encrypt(plaintext, associatedData, keyAsByte, iv);
    assertArrayEquals(expected, actual);
    assertThrows(
        NullPointerException.class, () -> Crypto.encrypt(null, associatedData, keyAsByte, iv));
  }

  @Test
  void decrypt() {}

  @Nested
  @DisplayName("Tests for computeHash")
  class computeHashTest {
    @Test
    @DisplayName("Case: Default")
    void computeHashDefault() {
      byte[] salt = Crypto.generateSecureByteArray(new Random().nextInt(64) + 1);
      int outputLength = 64;
      String password = generateRandomString(40);
      byte[] expected =
          SCrypt.generate(password.getBytes(), salt, (int) Math.pow(2, 18), 8, 1, outputLength);
      expected = Arrays.concatenate(salt, expected);
      byte[] actual = Crypto.computeHash(password, salt);
      assertArrayEquals(expected, actual);
      assertEquals(outputLength + salt.length, actual.length);
    }

    @Test
    @DisplayName("Case: no salt")
    void computeHashNoSalt() {
      byte[] salt = new byte[0];
      int outputLength = 64;
      String password = generateRandomString(new Random().nextInt(40) + 1);
      byte[] expected =
          SCrypt.generate(password.getBytes(), salt, (int) Math.pow(2, 18), 8, 1, outputLength);
      byte[] actual = Crypto.computeHash(password, salt);
      assertArrayEquals(expected, actual);
      assertEquals(outputLength, actual.length);
      actual = Crypto.computeHash(password, null);
      assertArrayEquals(expected, actual);
      assertEquals(outputLength, actual.length);
    }

    @Test
    @DisplayName("Case: empty or null plaintext")
    void computeHashEmpty() {
      byte[] salt = Crypto.generateSecureByteArray(new Random().nextInt(64) + 1);
      assertThrows(NullPointerException.class, () -> Crypto.computeHash(null, salt));
      Exception exception =
          assertThrows(IllegalArgumentException.class, () -> Crypto.computeHash("", salt));
      assertTrue(exception.getMessage().equals("empty input"));
    }
  }
}
