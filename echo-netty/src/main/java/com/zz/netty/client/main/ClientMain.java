package com.zz.netty.client.main;

import com.zz.netty.client.EchoClient;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new EchoClient().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
