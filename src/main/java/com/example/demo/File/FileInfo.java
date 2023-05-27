package com.example.demo.File;

import org.springframework.web.multipart.MultipartFile;

public class FileInfo {

    private String receiver;
    private String sender;
    private String fileKey;

    public FileInfo(String receiver, String sender, String fileKey) {
        this.receiver = receiver;
        this.sender = sender;
        this.fileKey = fileKey;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
