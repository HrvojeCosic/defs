package com.example.demo.Blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Blockchain {

    private static Blockchain instance = null;
    private List<Block> chain;
    private Set<String> nodes;

    private Blockchain() {
        chain = new ArrayList<>();
        createBlock(1, "0", "N.A", "N.A", "N.A");
        nodes = new HashSet<>();
        nodes.add("127.0.0.1:5111");
    }

    public static Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }
        return instance;
    }

    private Block createBlock(int nonce, String previousHash, String sender, String receiver, String fileHash) {
        Block block = new Block(chain.size() + 1, new Date().toString(), nonce, previousHash, sender, receiver, fileHash);
        chain.add(block);
        return block;
    }

    public String addFile(String sender, String receiver, String fileHash) {
        Block previousBlock = chain.get(chain.size() - 1);
        String previousHash = BlockchainUtils.hashBlock(previousBlock);
        int previousNonce = previousBlock.getNonce();
        int currNonce = previousBlock.mineBlock(previousNonce);

        Block block = createBlock(currNonce, previousHash, sender, receiver, fileHash);
        return block.getHash();
    }

    public boolean syncBlockchain() {
        List<Block> longestChain = chain;
        int maxLength = chain.size();
        boolean hasChanged = false;

        for (String node: nodes) {
            List<Block> nodeChain = new ArrayList<Block>(); // TODO: GET NODE's BLOCKCHAIN
            int length = nodeChain.size();

            if (length > maxLength && BlockchainUtils.isChainValid(nodeChain)) {
                maxLength = length;
                longestChain = nodeChain;
                hasChanged = true;
            }
        }

        return hasChanged;
    }
}
