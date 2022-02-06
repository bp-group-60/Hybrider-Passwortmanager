package tu.bp21.passwortmanager;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import org.bouncycastle.crypto.generators.SCrypt;


import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class CryptoTest {

  static {
    System.loadLibrary("Crypto");
  }

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  void generateSaltTest() {
    int saltSize = new Random().nextInt(64) + 1;
    byte[] salt = Crypto.generateSecureByteArray(saltSize);
    assertEquals(saltSize, salt.length);
    for (byte b : salt) {
      assertNotNull(b);
    }
  }

  @Test
  void encrypt() throws Exception {
    KeyGenerator keygen = KeyGenerator.getInstance("AES");
    keygen.init(256);
    SecretKey key = keygen.generateKey();

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

    cipher.init(Cipher.ENCRYPT_MODE, key);

  }

  @Test
  void decrypt() {
  }

  @Test
  void computeHashTest() {
    byte[] salt = Crypto.generateSecureByteArray(16);
    int outputLength = 64;
    Crypto.setSalt(salt);
    String password = generateRandomString(new Random().nextInt(20)+1);
    System.out.println("Wert");
    byte[] expected = SCrypt.generate(password.getBytes(),salt, (int) Math.pow(2,18), 8, 1, outputLength);
    System.out.println("Wert");
    expected = Arrays.concatenate(salt, expected);
    byte[] actual = Crypto.computeHash(password);
    assertArrayEquals(expected,actual);
    assertEquals(outputLength+salt.length, actual.length);
  }
}
