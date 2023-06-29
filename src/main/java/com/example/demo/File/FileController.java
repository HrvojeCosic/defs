package com.example.demo.File;

import com.example.demo.AES;
import com.example.demo.Blockchain.Block;
import com.example.demo.Blockchain.Blockchain;
import com.example.demo.IPFS.IpfsService;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file")
@CrossOrigin("http://localhost:3000/")
public class FileController {

    IpfsService ipfsService;
    private final SimpMessagingTemplate messagingTemplate;

    public FileController(IpfsService ipfsService, SimpMessagingTemplate messagingTemplate) {
        this.ipfsService = ipfsService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> newFile(
            @RequestPart FileInfo body,
            @RequestPart("file") MultipartFile reqFile
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
            String fileName = convFile.getAbsoluteFile().getName();
            File file = new File(fileId, fileHash, body.getFileKey(), body.getSender(), fileName);

            String ipfsHash = ipfsService.publishFile(reqFile);

            String blockHash = blockchain.addFile(file, ipfsHash);
            blockchain.syncBlockchain();

            messagingTemplate.convertAndSend("/topic/add_new_block", blockchain.getBlocks());

            return ResponseEntity.ok(String.format("file added with IPFS hash %s, and block hash %s", ipfsHash, blockHash));

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException
                 | IOException | BadPaddingException | InvalidKeyException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<Resource> download(@RequestBody DownloadFileRequest req) {
        try {
            Blockchain blockchain = Blockchain.getInstance();
            byte[] fileBytes = ipfsService.findFile(req.getIpfsHash());

            Block foundBlock = blockchain.getInternalBlocks()
                    .stream()
                    .filter(b -> Objects.equals(b.getFileValues(), req.getIpfsHash()))
                    .collect(Collectors.toList()).get(0);

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = String.format("%s-%s", dateFormat.format(currentDate), foundBlock.getFile().getFileName());

            java.io.File file = new java.io.File(fileName);
            FileUtils.writeByteArrayToFile(file, fileBytes);

            int length = 16;
            AES aes = new AES(req.getFileKey(), length);;

            byte[] decodedSecretKey = Base64.getDecoder().decode(foundBlock.getFile().getHash());
            aes.decrypt(file, decodedSecretKey);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(
                            URLConnection.guessContentTypeFromName(foundBlock.getFile().getFileName())
                    ))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName())
                    .body(new ByteArrayResource(fileBytes));

        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                 | NoSuchPaddingException | NoSuchAlgorithmException | IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(e.getMessage().getBytes()));
        }
    }
}
