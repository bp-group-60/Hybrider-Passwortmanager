package tu.bp21.passwortmanager;

import static org.junit.jupiter.api.Assertions.*;
import static tu.bp21.passwortmanager.StringFunction.generateRandomString;

import org.bouncycastle.crypto.generators.SCrypt;


import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
  static void setUp() {random = new Random();}

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

  @Nested
  @DisplayName("Tests for encrypt")
  class encryptTest{

    @Test
    @DisplayName("Case: Default")
    void encryptDefault() throws Exception {
      byte[] associatedData = new byte[random.nextInt(100)+1];
      random.nextBytes(associatedData);
      String plaintext = generateRandomString(40);
      checkEncryptExpected(associatedData, plaintext);
    }

    @Test
    @DisplayName("Case: empty or null associatedData")
    void encryptEmptyOrNullAssociatedData() throws Exception{
      byte[] associatedData = new byte[0];
      String plaintext = generateRandomString(40);
      checkEncryptExpected(associatedData, plaintext);
      checkEncryptExpected(null, plaintext);

    }

    @Test
    @DisplayName("Case: empty plaintext")
    void encryptEmptyPlaintext() throws Exception{
      byte[] associatedData = new byte[random.nextInt(100)+1];
      random.nextBytes(associatedData);
      String plaintext = "";
      checkEncryptExpected(associatedData, plaintext);
    }

    @Test
    @DisplayName("Case: null plaintext")
    void encryptNullPlainText(){
      byte[] associatedData = new byte[random.nextInt(100)+1];
      byte[] key = new byte[32];
      byte[] iv = new byte[12];
      random.nextBytes(associatedData);
      random.nextBytes(key);
      random.nextBytes(iv);
      assertThrows(NullPointerException.class, () -> Crypto.encrypt(null,associatedData,key,iv));
    }

    @Test
    @DisplayName("Case: illegal Key Size")
    void encryptIllegalKeySize(){
      int illegalSize = random.nextInt(100);
      while(illegalSize==32) illegalSize = random.nextInt(100);
      byte[] associatedData = new byte[random.nextInt(100)+1];
      byte[] key = new byte[illegalSize];
      byte[] iv = new byte[12];
      String plaintext = generateRandomString(40);
      random.nextBytes(associatedData);
      random.nextBytes(key);
      random.nextBytes(iv);
      byte[] expected = new byte[0];
      byte[] actual = Crypto.encrypt(plaintext,associatedData,key,iv);
      assertArrayEquals(expected,actual);
      assertEquals(0,actual.length);
    }

    @Test
    @DisplayName("Case: illegal IV Size")
    void encryptIllegalIVSize(){
      int illegalSize = random.nextInt(100);
      while(illegalSize==32) illegalSize = random.nextInt(100);
      byte[] associatedData = new byte[random.nextInt(100)+1];
      byte[] key = new byte[32];
      byte[] iv = new byte[illegalSize];
      String plaintext = generateRandomString(40);
      random.nextBytes(associatedData);
      random.nextBytes(key);
      random.nextBytes(iv);
      byte[] expected = new byte[0];
      byte[] actual = Crypto.encrypt(plaintext,associatedData,key,iv);
      assertArrayEquals(expected,actual);
      assertEquals(0,actual.length);
    }

    void checkEncryptExpected(byte[] associatedData, String plaintext) throws Exception{
      byte[] aadForExpected = associatedData;
      if(associatedData == null) aadForExpected = new byte[0];
      SecretKey key = generateKey();
      byte[] keyAsByte = key.getEncoded();
      byte[] expected = encryptWithGCM(plaintext, aadForExpected,key);

      byte[] iv = Arrays.copyOf(expected, 12);
      byte[] actual = Crypto.encrypt(plaintext,associatedData,keyAsByte,iv);

      assertArrayEquals(expected,actual);
    }

  }

  @Test
  void decrypt() {}


  @Nested
  @DisplayName("Tests for computeHash")
  class computeHashTest{
    @Test
    @DisplayName("Case: Default")
    void computeHashDefault() {
      byte[] salt = Crypto.generateSecureByteArray(random.nextInt(64)+1);
      int outputLength = 64;
      String password = generateRandomString(40);
      byte[] expected = computeExpectedHash(password,salt,outputLength);
      byte[] actual = Crypto.computeHash(password, salt);

      assertArrayEquals(expected, actual);
      assertEquals(outputLength + salt.length, actual.length);
    }

    @Test
    @DisplayName("Case: no salt")
    void computeHashNoSalt(){
      byte[] salt = new byte[0];
      int outputLength = 64;
      String password = generateRandomString(40);
      byte[] expected = computeExpectedHash(password,salt,outputLength);
      byte[] actual = Crypto.computeHash(password, salt);

      assertArrayEquals(expected, actual);
      assertEquals(outputLength, actual.length);

      actual = Crypto.computeHash(password, null);
      assertArrayEquals(expected, actual);
      assertEquals(outputLength, actual.length);
    }

    @Test
    @DisplayName("Case: empty or null plaintext")
    void computeHashEmpty(){
      byte[] salt = Crypto.generateSecureByteArray(random.nextInt(64)+1);
      int outputLength = 64;
      String password = "";
      byte[] expected = computeExpectedHash(password,salt,outputLength);
      byte[] actual = Crypto.computeHash(password, salt);

      assertArrayEquals(expected, actual);
      assertEquals(outputLength + salt.length, actual.length);

      assertThrows(NullPointerException.class, () -> Crypto.computeHash(null,salt));
    }

    byte[] computeExpectedHash(String plaintext, byte[] salt, int outputLength){
      byte[] expected =
              SCrypt.generate(plaintext.getBytes(), salt, (int) Math.pow(2, 18), 8, 1, outputLength);
      expected = Arrays.concatenate(salt, expected);
      return expected;
    }
  }

  SecretKey generateKey() throws Exception{
    KeyGenerator keygen = KeyGenerator.getInstance("AES");
    keygen.init(256);
    return keygen.generateKey();
  }

  byte[] encryptWithGCM(String plaintext, byte[] aad, SecretKey key) throws  Exception{
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    cipher.updateAAD(aad);
    byte[] iv = cipher.getIV();
    assertEquals(12,iv.length);
    byte[] expected = cipher.doFinal(plaintext.getBytes());
    expected = Arrays.concatenate(iv,expected);
    return expected;
  }
}
