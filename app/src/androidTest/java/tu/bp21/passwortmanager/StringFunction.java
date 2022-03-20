package tu.bp21.passwortmanager;

import java.util.Random;

public class StringFunction {

  public static String generateRandomString(int maxLength) {
    return generateRandomString(1,maxLength);
  }

  /**
   * this method generate a random numberalphabetic String with length between minLength and maxLength
   */
  public static String generateRandomString(int minLength, int maxLength) {
    Random random = new Random();
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = random.nextInt(maxLength-minLength+1) + minLength;

    String generatedString =
            random
                    .ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

    return generatedString;
  }

  public static String convertNullToEmptyString(String string) {
    if (string == null) string = "";
    return string;
  }
}
