package com.example.demo.P2P;

import com.example.demo.Blockchain.Block;
import com.example.demo.Blockchain.BlockchainUtils;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private String address;
    private final int port;
    private List<Block> blocks = new ArrayList<>();
    private ServerSocket socket;
    private boolean isActive;

    public Node(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void addBlock(Block block) {
        blocks.add(block);
        if (!BlockchainUtils.isChainValid(blocks)) {
            blocks.remove(block);
        }
    }

}
