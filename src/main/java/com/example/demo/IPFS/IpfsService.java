package com.example.demo.IPFS;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class IpfsService {

    IpfsConfig ipfsConfig;

    public IpfsService(IpfsConfig ipfsConfig) {
        this.ipfsConfig = ipfsConfig;
    }

    public String publishFile(MultipartFile file) throws IOException {
        IPFS ipfs = ipfsConfig.getIpfs();
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            MerkleNode response = ipfs.add(new NamedStreamable.InputStreamWrapper(inputStream)).get(0);
            return response.hash.toBase58();
    }

    public byte[] findFile(String hash) throws IOException {
            IPFS ipfs = ipfsConfig.getIpfs();
            Multihash pointer = Multihash.fromBase58(hash);
            return ipfs.cat(pointer);
    }
}
