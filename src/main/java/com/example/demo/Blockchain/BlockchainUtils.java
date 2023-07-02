package com.example.demo.Blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class BlockchainUtils {

    public static String hashBlock(Block block) {
        String dataToHash = block.getPreviousHash()
                + block.getTimestamp()
                + block.getNonce()
                + block.getFileValues();
        MessageDigest digest;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        assert bytes != null;
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public static boolean isChainValid(List<Block> chain) {
        Block previousBlock = chain.get(0);
        int blockIndex = 1;
        while (blockIndex < chain.size()) {
            Block block = chain.get(blockIndex);
            String currHashPrev = chain.get(blockIndex).getPreviousHash();
            String prevHash = previousBlock.getHash();
            if (!currHashPrev.equals(prevHash)) {
                return false;
            }
            if (!block.getHash().startsWith("0000")) {
                return false;
            }
            previousBlock = block;
            blockIndex++;
        }
        return true;
    }
}
