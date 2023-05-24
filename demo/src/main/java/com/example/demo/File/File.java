package com.example.demo.File;

public class File {
    private String id;
    private String hash;
    private String key;
    private String owner;

    public File(String id, String hash, String key, String owner) {
        this.id = id;
        this.hash = hash;
        this.key = key;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
