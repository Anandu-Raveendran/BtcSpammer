package com.anandu.btc.spammer.btcspammer;

import DTO.BtcPrice;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;

import static com.anandu.btc.spammer.btcspammer.Constants.*;
import static com.anandu.btc.spammer.btcspammer.Utils.sleepFor;
import static com.anandu.btc.spammer.btcspammer.Utils.writeToFile;

public class BtcRunner extends Thread {


    double old1 = 0F, old2 = 0F, old3 = 0F;
    int confidence = 0, oldConfidence = 0;
    double boughtAt = 0, topValueYet = 0;
    int fileLineCount = 0;
    boolean startTrading = false; // change to true when ready to buy and sell

    public void run() {

        while (true) {
            String saleStatus = null;
            if (Double.compare(BTCManagerSingleton.getInstance().currentPrice, old1) != 0) { // there is change in price
                old3 = old2; // replace old values one step
                old2 = old1;
                old1 = BTCManagerSingleton.getInstance().currentPrice;
                if (old3 > 0 && !startTrading) {
                    startTrading = true;
                    System.out.println("Trading is enabled as old3 is filled");
                }
            }

            sleepFor(1000);
            BTCManagerSingleton.getInstance().currentPrice = getPrice();
            if (BTCManagerSingleton.getInstance().currentPrice == -1) { // error getting price. then try again
                continue;
            }
            confidence = getConfidence(BTCManagerSingleton.getInstance().currentPrice, old1, old2, old3);

//            System.out.println(
//                    writeToFile("portfolio,txt", String.format("Current price: %f <br/>number of coins: %f <br/>Account Balance: %f <br/> portfolio: %f",
//                            BTCManagerSingleton.getInstance().currentPrice, BTCManagerSingleton.getInstance().numberOfCoins, BTCManagerSingleton.getInstance().accountBalance,
//                            BTCManagerSingleton.getInstance().currentPrice * BTCManagerSingleton.getInstance().numberOfCoins + BTCManagerSingleton.getInstance().accountBalance)));

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            if (startTrading) { // if it is ok to trade
                if (confidence > 0) { // if confidence is good
                    if (BTCManagerSingleton.getInstance().accountBalance > 0) {// and if there is still cash in account
                        double boughtFor = buyFor(BTCManagerSingleton.getInstance().accountBalance);
                        BTCManagerSingleton.getInstance().accountBalance -= boughtFor;// reduce account balance with the bought amount
                        BTCManagerSingleton.getInstance().numberOfCoins += boughtFor / BTCManagerSingleton.getInstance().currentPrice;
                        saleStatus = "Bought";
                    }
                }
                if (confidence < 0) {
                    if (BTCManagerSingleton.getInstance().numberOfCoins > 0) { //TODO:change this according to API. sell only if anything is there to sell
                        double soldfor = sellFor(BTCManagerSingleton.getInstance().numberOfCoins * BTCManagerSingleton.getInstance().currentPrice);
                        BTCManagerSingleton.getInstance().accountBalance += soldfor;
                        BTCManagerSingleton.getInstance().numberOfCoins = 0;
                        saleStatus = "Sold";
                    }
                }
            }
            BtcPrice btcPrice = new BtcPrice(BTCManagerSingleton.getInstance().currentPrice, confidence, timestamp, saleStatus);
            Gson gson = new Gson();

            System.out.println(writeToFile(priceList, gson.toJson(btcPrice)));
            fileLineCount++;

        }
    }

    //Returns -1 on error else returns the cost in string
    public double getPrice() {

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
    public int getConfidence(double current, double old1, double old2, double old3) {
        int confidence = 0;


        if (old3 > old2 && old2 >= old1 && old1 < current) // dip and increase
            confidence = 30;
        else if (old3 > old2 && old2 < old1 && old1 < current) // dip and increase
            confidence = 60;
        else if (old3 < old2 && old2 < old1 && old1 < current) // steady increase
            confidence = 90;


        if (boughtAt > 0) { // if bought
            if (current > topValueYet)
                topValueYet = current;
        } else { // if sold
            topValueYet = 0;
        }

        double threshold = topValueYet - lossLimit;

        if(current < threshold){
            confidence = - 90; // sell if it crosses loss limit
        }

        oldConfidence = confidence;
        return confidence;
    }


    //Buy
    public double buyFor(double amount) {
        System.out.println("buying for amount " + amount);
        boughtAt = amount;
        topValueYet = amount;
        return amount;
    }

    //Sell
    public double sellFor(double amount) {
        System.out.println("selling for amount " + amount);
        boughtAt = 0; //reset
        topValueYet = 0;

        return amount;
    }


}
