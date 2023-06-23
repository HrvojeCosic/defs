package com.example.demo;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AES {

    private final Cipher cipher;
    private final SecretKeySpec secretKey;

    public SecretKeySpec getSecretKey() {
        return secretKey;
    }
    public AES(String secret, int length) throws NoSuchPaddingException, NoSuchAlgorithmException {
        byte[] key = padSecret(secret, length);
        this.secretKey = new SecretKeySpec(key, "AES");
        this.cipher = Cipher.getInstance("AES");
    }

    public String encrypt(File file) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        write(file);
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public void decrypt(File file, byte[] dKey) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec spec = new SecretKeySpec(dKey,"AES");
        this.cipher.init(Cipher.DECRYPT_MODE, spec);
        write(file);
    }

    private void write(File file) throws IOException, IllegalBlockSizeException, BadPaddingException {
        int blockSize = cipher.getBlockSize();
        byte[] inputBuffer = new byte[blockSize];
        byte[] outputBuffer;

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {

            int bytesRead;
            while ((bytesRead = in.read(inputBuffer)) > 0) {
                outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                if (outputBuffer != null) {
                    out.write(outputBuffer);
                }
            }

            outputBuffer = cipher.doFinal();
            if (outputBuffer != null) {
                out.write(outputBuffer);
            }
        }
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
