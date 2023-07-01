package com.example.demo;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AES {

    private final Cipher cipher;
    private final SecretKeySpec secretKey;

    public AES(String secret, int length) throws NoSuchPaddingException, NoSuchAlgorithmException {
        byte[] key = padSecret(secret, length);
        this.secretKey = new SecretKeySpec(key, "AES");
        this.cipher = Cipher.getInstance("AES");
    }

    public byte[] encrypt(byte[] fileContents) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        return cipher.doFinal(fileContents);
    }

    public byte[] decrypt(byte[] encryptedFileBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return cipher.doFinal(encryptedFileBytes);
    }

    private byte[] padSecret(String str, int length) {
        if (str.length() < length) {
            char[] chars = new char[length];
            Arrays.fill(chars, ' ');
            str = str + new String(chars).substring(str.length());
        }
        return str.substring(0, length).getBytes(StandardCharsets.UTF_8);
    }
}
