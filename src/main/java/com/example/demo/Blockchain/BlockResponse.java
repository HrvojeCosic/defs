package com.example.demo.Blockchain;


public class BlockResponse {
    public BlockResponse(int blockIndex, String blockHash, String timestamp, int nonce, String previousHash, String fileId, String fileHash, String fileOwner, String fileValues) {
        this.blockIndex = blockIndex;
        this.blockHash = blockHash;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.fileId = fileId;
        this.fileHash = fileHash;
        this.fileOwner = fileOwner;
        this.fileValues = fileValues;
    }

    private int blockIndex;
    private String blockHash;
    private String timestamp;
    private int nonce;
    private String previousHash;
    private String fileId;
    private String fileHash;
    private String fileOwner;
    private String fileValues;

    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public void setFileOwner(String fileOwner) {
        this.fileOwner = fileOwner;
    }

    public void setFileValues(String fileValues) {
        this.fileValues = fileValues;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public String getBlockHash() {
        return blockHash;
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

    public String getFileId() {
        return fileId;
    }

    public String getFileHash() {
        return fileHash;
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public String getFileValues() {
        return fileValues;
    }

}
