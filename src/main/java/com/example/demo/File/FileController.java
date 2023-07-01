package com.example.demo.File;

import com.example.demo.AES;
import com.example.demo.Blockchain.Block;
import com.example.demo.Blockchain.Blockchain;
import com.example.demo.IPFS.IpfsService;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file")
public class FileController {

    IpfsService ipfsService;
    private final SimpMessagingTemplate messagingTemplate;

    public FileController(IpfsService ipfsService, SimpMessagingTemplate messagingTemplate) {
        this.ipfsService = ipfsService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> newFile(
            @RequestPart FileInfo body,
            @RequestPart("file") MultipartFile reqFile
    ) {
        Blockchain blockchain = Blockchain.getInstance();

        try {
            AES aes = new AES(body.getFileKey(), 16);

            byte[] encryptedFileBytes = aes.encrypt(reqFile.getBytes());

            MultipartFile encryptedFile = new MockMultipartFile(
                    Objects.requireNonNull(reqFile.getOriginalFilename()),
                    reqFile.getOriginalFilename(),
                    reqFile.getContentType(),
                    encryptedFileBytes
            );

            String ipfsHash = ipfsService.publishFile(encryptedFile);

            String encryptedFileHash = aes.encrypt(reqFile.getOriginalFilename().getBytes()).toString();
            String combinedData = String.format("%s%s",ipfsHash, encryptedFileHash);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combinedData.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String fileId = sb.toString();

            String fileName = reqFile.getOriginalFilename();
            File file = new File(fileId, encryptedFileHash, body.getFileKey(), body.getSender(), fileName);

            String blockHash = blockchain.addFile(file, ipfsHash);
            blockchain.syncBlockchain();

            messagingTemplate.convertAndSend("/topic/add_new_block", blockchain.getBlocks());

            return ResponseEntity.ok(String.format("file added with IPFS hash %s, and block hash %s", ipfsHash, blockHash));

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException
                 | IOException | BadPaddingException | InvalidKeyException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> download(@RequestParam String fileKey, @RequestParam String ipfsHash) {
        try {
            Blockchain blockchain = Blockchain.getInstance();
            byte[] encryptedFileBytes = ipfsService.findFile(ipfsHash);

            Block foundBlock = blockchain.getInternalBlocks()
                    .stream()
                    .filter(b -> Objects.equals(b.getFileValues(), ipfsHash))
                    .collect(Collectors.toList()).get(0);

            AES aes = new AES(fileKey, 16);
            byte[] decryptedFileBytes = aes.decrypt(encryptedFileBytes);

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = String.format("%s-%s", dateFormat.format(currentDate), foundBlock.getFile().getFileName());

            java.io.File file = new java.io.File(fileName);
            java.io.File tempFile = java.io.File.createTempFile("temp", null);
            FileUtils.writeByteArrayToFile(tempFile, decryptedFileBytes);
            ByteArrayResource resource = new ByteArrayResource(FileUtils.readFileToByteArray(tempFile));
            tempFile.delete();

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(
                            URLConnection.guessContentTypeFromName(foundBlock.getFile().getFileName())
                    ))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName())
                    .header("X-Suggested-Filename", file.getName())
                    .header("Access-Control-Expose-Headers", "X-Suggested-Filename")
                    .body(resource);

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                 | NoSuchPaddingException | NoSuchAlgorithmException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}