package com.example.demo.Blockchain;

import com.example.demo.File.File;

public class Block {

    private int index;
    private String timestamp;
    private long nonce;
    private String previousHash;
    private File fileInfo;
    private String fileValues;
    private String hash;

    public Block(int index, String timestamp, long nonce, String previousHash, File fileInfo, String fileValues) {
        this.index = index;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.fileInfo = fileInfo;
        this.fileValues = fileValues;
        this.hash = BlockchainUtils.hashBlock(this);
    }

public long mineBlock() {
        Blockchain blockchain = Blockchain.getInstance();
        int difficulty = blockchain.getDifficulty();
        long startTime = System.nanoTime();

        String target = "0".repeat(difficulty);
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = BlockchainUtils.hashBlock(this);
        }

        long endTime = System.nanoTime();
        blockchain.setLatestMiningTime((endTime - startTime) / 1_000_000_000);
        return nonce;
    }

    public int getIndex() {
        return index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getNonce() {
        return nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public File getFile() {
        return fileInfo;
    }

    public String getFileValues() {
        return fileValues;
    }

    public String getHash() {
        return hash;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

}