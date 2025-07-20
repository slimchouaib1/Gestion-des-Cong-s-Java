package tn.bfpme.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Use a fixed key for simplicity in this example
    private static final byte[] KEY = "MySuperSecretKey".getBytes(); // Ensure this is 16 bytes

    public static String encrypt(String input) throws Exception {
        return Base64.getEncoder().encodeToString(doCrypto(Cipher.ENCRYPT_MODE, input.getBytes()));
    }

    public static String decrypt(String input) throws Exception {
        byte[] decryptedBytes = doCrypto(Cipher.DECRYPT_MODE, Base64.getDecoder().decode(input));
        return new String(decryptedBytes);
    }

    private static byte[] doCrypto(int cipherMode, byte[] inputBytes) throws Exception {
        SecretKey secretKey = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);
        return cipher.doFinal(inputBytes);
    }
}

