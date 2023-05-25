package com.example.demo.Blockchain;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

public class BlockchainUtils {

    public static String hashBlock(Block b) {
        return DigestUtils.sha256Hex(
                String.format("%s%s%s%s", b.getPreviousHash(), b.getTimestamp(), b.getNonce(), b.getFileValues())
        );
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
