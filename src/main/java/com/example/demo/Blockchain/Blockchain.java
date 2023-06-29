package com.example.demo.Blockchain;

import com.example.demo.File.File;
import com.example.demo.P2P.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Blockchain {

    private static Blockchain instance = null;
    private List<Block> chain;
    private final List<Node> nodes;

    private Blockchain() {
        Block genesis = new Block(0, "", 0, "N.A", new File("N.A", "N.A", "N.A", "N.A", "N.A"), "N.A");

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
                        block.getNonce(), block.getPreviousHash(), block.getFile().getId(),
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
        int previousNonce = previousBlock.getNonce();
        int currNonce = previousBlock.mineBlock(previousNonce);

        Block block = createBlock(currNonce, previousHash, file, ipfsHash);
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
}
