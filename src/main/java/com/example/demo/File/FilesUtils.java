package com.example.demo.File;

import com.example.demo.Blockchain.Block;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilesUtils {
    public static String generateFileId(String ipfsHash, String encryptedFileHash) throws NoSuchAlgorithmException {
        String combinedData = String.format("%s%s",ipfsHash, encryptedFileHash);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(combinedData.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String generateFileName(Block block) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName =  String.format("%s-%s", dateFormat.format(currentDate), block.getFile().getFileName());
        return new java.io.File(fileName).getName();
    }

    public static ByteArrayResource generateResource(byte[] fileBytes) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("temp", null);
        FileUtils.writeByteArrayToFile(tempFile, fileBytes);
        ByteArrayResource resource = new ByteArrayResource(FileUtils.readFileToByteArray(tempFile));
        tempFile.delete();
        return resource;
    }
}
