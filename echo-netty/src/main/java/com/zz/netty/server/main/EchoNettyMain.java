package com.zz.netty.server.main;

import com.zz.netty.server.EchoNettyServer;

public class EchoNettyMain {

    public static void main(String[] args) {
        EchoNettyServer nettyServer = new EchoNettyServer();
        try {
            nettyServer.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
