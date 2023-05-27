package com.example.demo.Blockchain;

import com.example.demo.File.File;

public class Block {

    private int index;
    private String timestamp;
    private int nonce;
    private String previousHash;
    private File fileInfo;
    private String fileValues;
    private String hash;

    public Block(int index, String timestamp, int nonce, String previousHash, File fileInfo, String fileValues) {
        this.index = index;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.fileInfo = fileInfo;
        this.fileValues = fileValues;
        this.hash = BlockchainUtils.hashBlock(this);
    }

    public int mineBlock(int difficulty) {
        String target = "0".repeat(difficulty);

        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = BlockchainUtils.hashBlock(this);
        }

        return nonce;
    }



    public int getIndex() {
        return index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getNonce() {
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

}