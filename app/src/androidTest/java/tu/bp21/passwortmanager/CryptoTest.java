package tu.bp21.passwortmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

class CryptoTest {

  static {
    System.loadLibrary("Crypto");
  }

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  void generateSalt() {
    int saltSize = new Random().nextInt(64) + 1;
    byte[] salt = Crypto.generateSalt(saltSize);
    assertEquals(saltSize, salt.length);
    for (byte b : salt) {
      assertNotNull(b);
    }
  }

  /*@Test
  void setSalt() {
  }

  @Test
  void encrypt() {
  }

  @Test
  void decrypt() {
  }

  @Test
  void computeHash() {
  }*/
}
