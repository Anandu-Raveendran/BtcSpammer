package com.anandu.btc.spammer.btcspammer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.anandu.btc.spammer.btcspammer.Constants.priceList;

@SpringBootApplication
public class BtcspammerApplication {


    public static void main(String[] args) {

        SpringApplication.run(BtcspammerApplication.class, args);

        if (true) { // delete or not
            Path fileToDeletePath = Paths.get(priceList);
            try {
                Files.delete(fileToDeletePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BTCManagerSingleton.getInstance().accountBalance = 1000;

        BtcRunner btcRunner = new BtcRunner();
        btcRunner.start();
    }


}
