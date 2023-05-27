package com.example.demo.File;

import com.example.demo.AES;
import com.example.demo.Blockchain.Blockchain;
import com.example.demo.IPFS.IpfsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RestController
@RequestMapping("/api/file")
public class FileController {

    IpfsService ipfsService;
    private final SimpMessagingTemplate messagingTemplate;

    public FileController(IpfsService ipfsService, SimpMessagingTemplate messagingTemplate) {
        this.ipfsService = ipfsService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping()
    public ResponseEntity<String> newFile(
            @ModelAttribute FileInfo body,
            @RequestParam("file") MultipartFile reqFile
    ) {
        Blockchain blockchain = Blockchain.getInstance();

        try {
            AES aes = new AES(body.getFileKey(), 16);

            java.io.File convFile = new java.io.File(Objects.requireNonNull(reqFile.getOriginalFilename()));
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(reqFile.getBytes());
            fos.close();

            String fileHash = aes.encrypt(convFile);
            String fileId = String.format("%d%s", blockchain.getBlocks().size(), fileHash);
            File file = new File(fileId, fileHash, body.getFileKey(), body.getSender());

            String ipfsHash = ipfsService.publishFile(reqFile);

            String blockHash = blockchain.addFile(file, ipfsHash);
            blockchain.syncBlockchain();

            messagingTemplate.convertAndSend("/topic/add_new_block", blockchain.getBlocks());

            return ResponseEntity.ok(String.format("file added with IPFS hash %s, and block hash %s", ipfsHash, blockHash));

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException | BadPaddingException | InvalidKeyException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
