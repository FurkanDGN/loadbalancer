package com.furkanaxx34.gmail.loadbalancer;

import lombok.Data;

@Data
public class BackendServer {

    private String ip;
    private int port;

    public BackendServer(String address) {
        String[] split = address.split(":");
        if (split.length < 1)
            throw new IllegalArgumentException("Invalid inet address format");

        this.ip = split[0];
        this.port = Integer.parseInt(split[1]);
    }

    public BackendServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getAddress() {
        return this.ip + ":" + this.port;
    }
}
