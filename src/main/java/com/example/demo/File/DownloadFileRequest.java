package com.example.demo.File;

public class DownloadFileRequest {
    private String fileKey;
    private String ipfsHash;

    public String getFileKey() {
        return fileKey;
    }

    public String getIpfsHash() {
        return ipfsHash;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public void setIpfsHash(String ipfsHash) {
        this.ipfsHash = ipfsHash;
    }
}
