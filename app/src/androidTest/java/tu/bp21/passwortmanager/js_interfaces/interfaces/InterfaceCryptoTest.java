package tu.bp21.passwortmanager.js_interfaces.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.BaseEncoding;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static tu.bp21.passwortmanager.StringFunction.*;

import tu.bp21.passwortmanager.cryptography.Crypto;
import tu.bp21.passwortmanager.js_interfaces.interfaces.InterfaceCrypto;

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
  class hashPasswordTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/Crypto/hashPassword.csv", numLinesToSkip = 1)
    @DisplayName("Case:")
    void hashPasswordTest(String displayCase, String userPassword, String salt) {
      userPassword = convertNullToEmptyString(userPassword);
      salt = convertNullToEmptyString(salt);
      byte[] byteSalt = BaseEncoding.base16().decode(salt);
      String expected = BaseEncoding.base16().encode(Crypto.computeHash(userPassword, byteSalt));
      String actual = interfaceCrypto.hashPassword(userPassword, salt);
      assertEquals(expected, actual);
    }
  }

  @Nested
  @DisplayName("Test for generateKey")
  class generateKeyTest {
    @ParameterizedTest
    @CsvFileSource(resources = "/Crypto/generateKey.csv", numLinesToSkip = 1)
    @DisplayName("Case:")
    void generateKeyTest(String displayCase, String passwordToDerive, String salt) {
      passwordToDerive = convertNullToEmptyString(passwordToDerive);
      salt = convertNullToEmptyString(salt);
      byte[] byteSalt = BaseEncoding.base16().decode(salt);
      String expected =
          BaseEncoding.base16().encode(Crypto.generateKey(passwordToDerive, byteSalt));
      String actual = interfaceCrypto.generateKey(passwordToDerive, salt);
      assertEquals(expected, actual);
    }
  }
}
