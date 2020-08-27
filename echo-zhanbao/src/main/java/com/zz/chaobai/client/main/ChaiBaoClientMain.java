package com.zz.chaobai.client.main;

import com.zz.chaobai.client.ChaiBaoClient;

public class ChaiBaoClientMain {
    public static void main(String[] args) {
        try {
            new ChaiBaoClient().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
