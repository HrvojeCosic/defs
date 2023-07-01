package com.example.demo.File;

import com.example.demo.AES;
import com.example.demo.Blockchain.Block;
import com.example.demo.Blockchain.Blockchain;
import com.example.demo.IPFS.IpfsService;
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
import java.security.NoSuchAlgorithmException;

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
            String encryptedFileHash = encryptedFileBytes.toString();

            MultipartFile encryptedFile = new MockMultipartFile(
                    Objects.requireNonNull(reqFile.getOriginalFilename()),
                    reqFile.getOriginalFilename(),
                    reqFile.getContentType(),
                    encryptedFileBytes
            );
            String ipfsHash = ipfsService.publishFile(encryptedFile);

            String fileName = reqFile.getOriginalFilename();
            File file = new File(
                    FilesUtils.generateFileId(ipfsHash, encryptedFileHash),
                    encryptedFileHash,
                    body.getFileKey(),
                    body.getSender(),
                    fileName
            );

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

            String fileName = FilesUtils.generateFileName(foundBlock);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(
                            URLConnection.guessContentTypeFromName(foundBlock.getFile().getFileName())
                        )
                    )
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName)
                    .header("X-Suggested-Filename", fileName)
                    .header("Access-Control-Expose-Headers", "X-Suggested-Filename")
                    .body(FilesUtils.generateResource(decryptedFileBytes));

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                 | NoSuchPaddingException | NoSuchAlgorithmException | IOException e) {
            return new ResponseEntity<>(
                    "Couldn't get the file. Make sure the provided file key is correct and try again",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}