package com.example.demo.Blockchain;

import com.example.demo.File.File;
import com.example.demo.P2P.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Blockchain {

    private final int MINING_MAX_TIME = 10;
    private final int MINING_MIN_TIME = 5;
    private final int MINING_MAX_DIFFICULTY = 5;

    private static Blockchain instance = null;
    private List<Block> chain;
    private final List<Node> nodes;
    private long latestBlockMiningTime;
    private int latestDifficulty;

    private Blockchain() {
        Block genesis = new Block(1, "", 0, "N.A", new File("N.A", "N.A", "N.A", "N.A", "N.A"), "N.A");

        chain = new ArrayList<>();
        nodes = new ArrayList<>();

        chain.add(genesis);
    }

    public static synchronized Blockchain getInstance() {
        if (instance == null) {
            instance = new Blockchain();
        }
        return instance;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<BlockResponse> getBlocks() {
        return chain.stream()
                .map(block -> new BlockResponse(
                        block.getIndex(), block.getHash(), block.getTimestamp(),
                        (int)block.getNonce(), block.getPreviousHash(), block.getFile().getId(),
                        block.getFile().getHash(), block.getFile().getOwner(), block.getFileValues()
                    )
                ).collect(Collectors.toList());
    }

    public List<Block> getInternalBlocks() {
        return chain;
    }

    private Block createBlock(int nonce, String previousHash, File file, String ipfsHash) {
        Block block = new Block(chain.size() + 1, new Date().toString(), nonce, previousHash, file, ipfsHash);
        chain.add(block);
        return block;
    }

    public String addFile(File file, String ipfsHash) {
        Block previousBlock = chain.get(chain.size() - 1);
        String previousHash = BlockchainUtils.hashBlock(previousBlock);
        Block block = createBlock(0, previousHash, file, ipfsHash);
        long currNonce = block.mineBlock();
        block.setNonce(currNonce);
        return block.getHash();
    }

   public void syncBlockchain() {
        int maxLength = chain.size();

        for (Node node: nodes) {
            List<Block> nodeChain = node.getBlocks();
            int length = nodeChain.size();

            if (length > maxLength && BlockchainUtils.isChainValid(nodeChain)) {
                maxLength = length;
                chain = nodeChain;
            }
        }

       for (Node node: nodes) {
           node.setBlocks(chain);
       }
   }

   public void setLatestMiningTime(long time) {
        latestBlockMiningTime = time;
   }

   public int getDifficulty() {
        if (latestBlockMiningTime < MINING_MIN_TIME && latestDifficulty < MINING_MAX_DIFFICULTY) {
            latestDifficulty++;
        } else if (latestBlockMiningTime > MINING_MAX_TIME) {
            latestDifficulty--;
        }
        return latestDifficulty;
   }
}
