package tu.bp21.passwortmanager.js_interfaces.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static tu.bp21.passwortmanager.StringFunction.*;

import tu.bp21.passwortmanager.cryptography.Crypto;

class InterfaceCryptoTest {
  static {
    System.loadLibrary("Crypto");
  }

  InterfaceCrypto interfaceCrypto;

  @BeforeEach
  void setup() {
    interfaceCrypto = new InterfaceCrypto();
  }

  @Nested
  @DisplayName("Test for hashPassword")
  class HashPasswordTests {

    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceCryptoTest/hashPassword.csv", numLinesToSkip = 1)
    @DisplayName("Case:")
    void hashPasswordTest(String displayCase, String plainUserPassword, String salt) {
      plainUserPassword = convertNullToEmptyString(plainUserPassword);
      salt = convertNullToEmptyString(salt);
      byte[] byteSalt = BaseEncoding.base16().decode(salt);
      String expected =
          BaseEncoding.base16().encode(Crypto.computeHash(plainUserPassword, byteSalt));
      String actual = interfaceCrypto.hashUserPassword(plainUserPassword, salt);
      assertEquals(expected, actual);
    }
  }

  @Nested
  @DisplayName("Test for generateKey")
  class GenerateKeyTest {
    @ParameterizedTest
    @CsvFileSource(resources = "/InterfaceCryptoTest/generateKey.csv", numLinesToSkip = 1)
    @DisplayName("Case:")
    void generateKeyTest(String displayCase, String passwordToDerive, String salt) {
      passwordToDerive = convertNullToEmptyString(passwordToDerive);
      salt = convertNullToEmptyString(salt);
      byte[] byteSalt = BaseEncoding.base16().decode(salt);
      String expected =
          BaseEncoding.base16().encode(Crypto.generateKey(passwordToDerive, byteSalt));
      String actual = interfaceCrypto.generateEncryptionKey(passwordToDerive, salt);
      assertEquals(expected, actual);
    }
  }
}
