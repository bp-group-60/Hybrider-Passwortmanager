package tu.bp21.passwortmanager;

/** parameters used for cryptography algorithms and tests are defined here */
public class Constants {
  // length in byte length

  public static final int SALT_LENGTH = 16;

  // parameters for scrypt
  // Parameter N as the n power of 2 for scrypt parameter N, for example 2^n
  // total ram cost = 128 * 2^n * r * p bytes
  public static final int SCRYPT_PARAM_N = 18;
  public static final int SCRYPT_PARAM_R = 8;
  public static final int SCRYPT_PARAM_P = 1;
  // length of hash value of user password stored in database
  public static final int HASH_OUTPUT_LENGTH = 64;

  // parameters for PBKDF2
  public static final int PBKDF2_ITERATIONS = 500000;
  // output length must match encryption key length (GCM)
  public static final int KEY_OUTPUT_LENGTH = 32;

  // parameters for AES GCM
  public static final int ENCRYPT_IV_LENGTH = 12;
  public static final int ENCRYPT_KEY_LENGTH = 32;
  public static final int GCM_TAG_LENGTH = 16;
}
