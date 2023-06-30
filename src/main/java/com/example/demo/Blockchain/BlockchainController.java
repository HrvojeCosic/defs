package com.example.demo.Blockchain;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
@CrossOrigin("http://localhost:3000/")
public class BlockchainController {
    @GetMapping
    public ResponseEntity<List<BlockResponse>> getBlockchain() {
        Blockchain blockchain = Blockchain.getInstance();
        return ResponseEntity.ok(blockchain.getBlocks());
    }
}
