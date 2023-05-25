package com.example.demo.IPFS;

import io.ipfs.api.IPFS;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IpfsConfig {

    IPFS ipfs;

    public IpfsConfig() {
        ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
    }

}