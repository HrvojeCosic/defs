package com.example.demo.Blockchain;

public class Block {

    private int index;
    private String timestamp;
    private int nonce;
    private String previousHash;
    private String sender;
    private String receiver;
    private String fileValues;
    private String hash;

    public Block(int index, String timestamp, int nonce, String previousHash, String sender, String receiver, String fileValues) {
        this.index = index;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.sender = sender;
        this.receiver = receiver;
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

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getFileValues() {
        return fileValues;
    }

    public String getHash() {
        return hash;
    }

}