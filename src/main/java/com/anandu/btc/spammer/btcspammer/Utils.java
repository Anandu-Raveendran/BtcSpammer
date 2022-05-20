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

    //Returns -1 on error else returns the cost in string
    public static Float getPrice() {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.coinbase.com/v2/prices/spot?currency=USD";
        float price = -1F;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        //System.out.println(response.getBody());
        if (response.getStatusCode() == HttpStatus.OK && !response.getBody().isEmpty() && response.getBody().length() > 52) {
            String priceStr = response.getBody().substring(49, response.getBody().length() - 3);
            price = Float.parseFloat(priceStr);
        } else {
            System.out.println("Error getting price data code " + response.getStatusCode() + " " + response.getBody());
        }
        return price;
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
//            System.out.println("Sleeping for " + seconds + " second(s)");
        } catch (InterruptedException e) {
            System.out.println("Error sleeping " + e.getMessage());
            e.printStackTrace();
        }
    }

    //get confidence of going up and down. positive confidence for up and negative for down
    //confidence is from -100(will sure go down) to 100(will sure go up)
    public static int getConfidence(Float current, Float old1, Float old2, Float old3) {
        int confidence = 0;

        if (current > old1) {
            confidence += 30;
        }
        if (old1 > old2) {
            confidence += 30;
        }
        if (old2 > old3) {
            confidence += 30;
        }


        if (current < old1) {
            confidence -= 30;
        }
        if (old1 < old2) {
            confidence -= 30;
        }
        if (old2 < old3) {
            confidence -= 30;
        }


        return confidence;
    }


    //Buy
    public static float buyFor(float amount) {
        System.out.println("buying for amount " + amount);
        return amount;
    }

    //Sell
    public static float sellFor(float amount) {
        System.out.println("selling for amount " + amount);
        return amount;
    }

}
