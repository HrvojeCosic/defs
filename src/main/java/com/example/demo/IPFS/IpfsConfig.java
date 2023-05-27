package com.example.demo.IPFS;

import io.ipfs.api.IPFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IpfsConfig {

    private final String ipfsAddress;

    public IpfsConfig(@Value("${ipfs.host:127.0.0.1}") String ipfsHost,
                      @Value("${ipfs.port:5001}") String ipfsPort) {
        ipfsAddress = String.format("/ip4/%s/tcp/%s", ipfsHost, ipfsPort);
    }

    public IPFS getIpfs() {
        return new IPFS(ipfsAddress);
    }
}