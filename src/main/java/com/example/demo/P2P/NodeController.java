package com.example.demo.P2P;

import com.example.demo.Blockchain.Blockchain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/node")
public class NodeController {

    ObjectMapper mapper = new ObjectMapper();

    @MessageMapping("/add_client_node")
    @SendTo("/topic/add_client_node")
    public ResponseEntity<String> addClientNode(@Payload Node clientNode) {
        Blockchain blockchain = Blockchain.getInstance();
        blockchain.getNodes().add(clientNode);
        blockchain.syncBlockchain();

        try {
            String res = mapper.writeValueAsString(blockchain.getNodes());
            return ResponseEntity.ok(res);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @MessageMapping("/remove_client_node")
    @SendTo("/topic/my_response")
    public ResponseEntity<String> removeClientNode(@Payload Node clientNode) {
        Blockchain blockchain = Blockchain.getInstance();
        blockchain.getNodes().remove(clientNode);

        try {
            String res = mapper.writeValueAsString(blockchain.getNodes());
            return ResponseEntity.ok(res);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
