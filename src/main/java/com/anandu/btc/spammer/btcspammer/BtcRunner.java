package com.anandu.btc.spammer.btcspammer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;

import static com.anandu.btc.spammer.btcspammer.Utils.sleepFor;
import static com.anandu.btc.spammer.btcspammer.Utils.writeToFile;

public class BtcRunner extends Thread {

    String filename = "priceList.txt";
    String tradingReportfilename = "tradeReport.txt";

    float price = 0F, old1 = 0F, old2 = 0F, old3 = 0F;
    int confidence;
    boolean startTrading = false; // change to true when ready to buy and sell
    float accountBalance = 1000;

    public void run() {

        while (true) {
            if (Float.compare(price, old1) != 0) { // there is change in price
                old3 = old2; // replace old values one step
                old2 = old1;
                old1 = price;
                if (old3 > 0 && !startTrading) {
                    startTrading = true;
                    System.out.println("Trading is enabled as old3 is filled");
                }
            }

            sleepFor(1);
            price = getPrice();
            if (price == -1) { // error getting price. then try again
                continue;
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            confidence = getConfidence(price, old1, old2, old3);

            System.out.println(
                    writeToFile(filename,          // write to file
                            old3 + ", " + old2 + ", " + old1 + ", " +
                                    price + ", " +
                                    confidence + ", " +
                                    timestamp)
            )
            ;

            if (startTrading) { // if it is ok to trade
                if (confidence > 30) { // if confidence is good
                    if (accountBalance > 0) {// and if there is still cash in account
                        accountBalance -= buyFor(accountBalance); // reduce account balance with the bought amount
                        writeToFile(tradingReportfilename, "Buying, " + String.valueOf(price));
                    }
                }
                if (confidence < 30) {
                    if (accountBalance < 1000) { //TODO:change this according to API. sell only if anything is there to sell
                        accountBalance += sellFor(accountBalance);
                        writeToFile(tradingReportfilename, "selling, -" + price);
                    }
                }
            }
        }
    }

    //Returns -1 on error else returns the cost in string
    public float getPrice() {

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


    //get confidence of going up and down. positive confidence for up and negative for down
    //confidence is from -100(will sure go down) to 100(will sure go up)
    public int getConfidence(Float current, Float old1, Float old2, Float old3) {
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
    public float buyFor(float amount) {
        System.out.println("buying for amount " + amount);
        return amount;
    }

    //Sell
    public float sellFor(float amount) {
        System.out.println("selling for amount " + amount);
        return amount;
    }


}
