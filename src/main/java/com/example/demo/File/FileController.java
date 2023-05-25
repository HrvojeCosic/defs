package com.example.demo.File;

import com.example.demo.AES;
import com.example.demo.Blockchain.Blockchain;
import com.example.demo.IPFS.IpfsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/file")
public class FileController {

    IpfsService ipfsService;

    public FileController(IpfsService ipfsService) {
        this.ipfsService = ipfsService;
    }

    @PostMapping
    public ResponseEntity<String> newFile(
            @RequestParam("file")MultipartFile file,
            @RequestBody FileInfo body
    ) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException {
        AES aes = new AES("secret123", 16);
        aes.encrypt((File) file);

        String ipfsHash = ipfsService.publishFile(file);

        Blockchain blockchain = Blockchain.getInstance();
        String blockHash = blockchain.addFile(body.getSender(), body.getReceiver(), ipfsHash);

        return ResponseEntity.ok(String.format("file added with IPFS hash %s, and block hash %s", ipfsHash, blockHash));
    }
}
