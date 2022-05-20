package com.anandu.btc.spammer.btcspammer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static com.anandu.btc.spammer.btcspammer.Utils.*;

@SpringBootApplication
public class BtcspammerApplication {


    public static void main(String[] args) {

        SpringApplication.run(BtcspammerApplication.class, args);

//        BtcRunner btcRunner = new BtcRunner();
//        btcRunner.start();

    }


}
