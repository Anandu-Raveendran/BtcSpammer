package com.anandu.btc.spammer.btcspammer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class Utils {

    public static String writeToFile(String fileName, String data) {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(data);
            writer.newLine();
            writer.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldnt write to file " + fileName +
                    ". data: " + data + " reason: " + e.getMessage());
            return "";
        }
    }


    public static void sleepFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
//            System.out.println("Sleeping for " + seconds + " second(s)");
        } catch (InterruptedException e) {
            System.out.println("Error sleeping " + e.getMessage());
            e.printStackTrace();
        }
    }


}
