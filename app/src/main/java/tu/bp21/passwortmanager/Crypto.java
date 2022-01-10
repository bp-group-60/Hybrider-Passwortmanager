package tu.bp21.passwortmanager;

public class Crypto {

    public native byte[] crypt(byte[] input, byte[] key, byte[] iv, boolean shouldEncrypt);
}
