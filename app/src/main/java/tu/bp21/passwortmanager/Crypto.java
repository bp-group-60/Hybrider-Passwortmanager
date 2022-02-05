package tu.bp21.passwortmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tu.bp21.passwortmanager.db.Password;
import tu.bp21.passwortmanager.db.dao.PasswordDao;

public class Crypto {
    private static byte[] salt;
    private static byte[] key;
    private static String username;
    private static PasswordDao passwordDao;

    private static native byte[] crypt(byte[] input, byte[] aad, byte[] iv, byte[] key);
    private static native byte[] generateKey(byte[] input, int input_length, byte[] salt, int salt_length);
    public static native byte[] generateSecureByteArray(int size);
    private static native byte[] hash(byte[] input, int input_length, byte[] salt, int salt_length);

    public static void setPasswordDao(PasswordDao passwordDao){
        Crypto.passwordDao = passwordDao;
    }

    public static void setGeneratedKey(String passwordToDerive){
        byte[] input = passwordToDerive.getBytes();
        key = generateKey(input, input.length, salt, salt.length);
    }

    public static void setSalt(byte[] input){
        salt = input;
    }

    public static byte[] encrypt(String text){
        byte[] input = text.getBytes();
        return crypt(input, username.getBytes(), generateIV(), key);
    }

    public static String decrypt(byte[] cipher){
        byte[] text = crypt(cipher, username.getBytes(), null,  key);
        return new String(text);
    }

    public static byte[] computeHash(String password){
        byte[] input = password.getBytes();
        byte[] output = hash(input, input.length, salt, salt.length);
        if(output==null)
            throw new RuntimeException("Hash failed. Not enough RAM");
        return output;
    }

    public static void setCurrentUser(String username){
        Crypto.username = username;
    }

    private static byte[] generateIV(){
        List<Password> list = passwordDao.getPasswordList(username);
        ArrayList<byte[]> ivList = new ArrayList<>();
        int length = list.size();
        if(length >= Math.pow(2,96))
            throw new RuntimeException("Reached maximum number of entries for this account");
        for (Password x : list) {
            ivList.add(Arrays.copyOf(x.password,12));
        }
        byte[] iv = generateSecureByteArray(12);
        while(ivList.contains(iv))
            iv = generateSecureByteArray(12);
        return iv;
    }

}
