package com.example.demo.File;


public class FileInfo {

    private String sender;
    private String fileKey;

    public FileInfo(String sender, String fileKey) {
        this.sender = sender;
        this.fileKey = fileKey;
    }

    public FileInfo() {

    }


    public String getSender() {
        return sender;
    }

    public String getFileKey() {
        return fileKey;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
