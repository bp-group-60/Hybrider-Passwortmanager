package tu.bp21.passwortmanager;

public class Crypto {
    private static byte[] salt;
    private static byte[] key;

    private static native byte[] crypt(byte[] input, byte[] key, boolean shouldEncrypt);
    private static native byte[] generateKey(byte[] input, int input_length, byte[] salt, int salt_length, int iterations);
    public static native byte[] generateSalt(int size);
    private static native byte[] hash(byte[] input, int input_length, byte[] salt, int salt_length);

    public static void setKey(String password){
        int iterations = 1000000;
        byte[] input = password.getBytes();
        key = generateKey(input, input.length, salt, salt.length, iterations);
    }

    public static void setSalt(byte[] input){
        salt = input;
    }

    public static byte[] encrypt(String text){
        byte[] input = text.getBytes();
        return crypt(input, key, true);
    }

    public static String decrypt(byte[] cipher){
        byte[] text = crypt(cipher, key, false);
        return new String(text);
    }

    public static byte[] computeHash(String password){
        byte[] input = password.getBytes();
        return hash(input, input.length, salt, salt.length);
    }

}
