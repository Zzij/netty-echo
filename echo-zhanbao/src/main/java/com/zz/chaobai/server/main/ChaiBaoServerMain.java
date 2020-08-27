package com.zz.chaobai.server.main;

import com.zz.chaobai.server.ChaiBaoServer;

public class ChaiBaoServerMain {
    public static void main(String[] args) {
        try {
            new ChaiBaoServer().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
