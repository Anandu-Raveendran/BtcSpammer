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

    double old1 = 0F, old2 = 0F, old3 = 0F;
    int confidence;
    boolean startTrading = false; // change to true when ready to buy and sell

    public void run() {

        while (true) {
            if (Double.compare(BTCManagerSingleton.getInstance().currentPrice, old1) != 0) { // there is change in price
                old3 = old2; // replace old values one step
                old2 = old1;
                old1 = BTCManagerSingleton.getInstance().currentPrice;
                if (old3 > 0 && !startTrading) {
                    startTrading = true;
                    System.out.println("Trading is enabled as old3 is filled");
                }
            }

            sleepFor(1);
            BTCManagerSingleton.getInstance().currentPrice = getPrice();
            if (BTCManagerSingleton.getInstance().currentPrice == -1) { // error getting price. then try again
                continue;
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            confidence = getConfidence(BTCManagerSingleton.getInstance().currentPrice, old1, old2, old3);

            System.out.println(
                    writeToFile(filename,          // write to file
                            BTCManagerSingleton.getInstance().currentPrice + ", " +
                                    confidence + ", " +
                                    timestamp)
            )
            ;

            System.out.println(
                    writeToFile("portfolio,txt", String.format("Current price: %f <br/>number of coins: %f <br/>Account Balance: %f <br/> portfolio: %f",
                            BTCManagerSingleton.getInstance().currentPrice, BTCManagerSingleton.getInstance().numberOfCoins, BTCManagerSingleton.getInstance().accountBalance,
                            BTCManagerSingleton.getInstance().currentPrice * BTCManagerSingleton.getInstance().numberOfCoins + BTCManagerSingleton.getInstance().accountBalance)));


            if (startTrading) { // if it is ok to trade
                if (confidence > 30) { // if confidence is good
                    if (BTCManagerSingleton.getInstance().accountBalance > 0) {// and if there is still cash in account
                        double boughtFor = buyFor(BTCManagerSingleton.getInstance().accountBalance);
                        BTCManagerSingleton.getInstance().accountBalance -= boughtFor;// reduce account balance with the bought amount
                        BTCManagerSingleton.getInstance().numberOfCoins += boughtFor / BTCManagerSingleton.getInstance().currentPrice;
                        writeToFile(tradingReportfilename, "Buying, " + String.valueOf(BTCManagerSingleton.getInstance().currentPrice));
                    }
                }
                if (confidence < 0) {
                    if (BTCManagerSingleton.getInstance().numberOfCoins > 0) { //TODO:change this according to API. sell only if anything is there to sell
                        double soldfor = sellFor(BTCManagerSingleton.getInstance().numberOfCoins * BTCManagerSingleton.getInstance().currentPrice);
                        BTCManagerSingleton.getInstance().accountBalance += soldfor;
                        BTCManagerSingleton.getInstance().numberOfCoins = 0;
                        writeToFile(tradingReportfilename, "selling, -" + BTCManagerSingleton.getInstance().currentPrice);
                    }
                }
            }
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
    public double buyFor(double amount) {
        System.out.println("buying for amount " + amount);
        return amount;
    }

    //Sell
    public double sellFor(double amount) {
        System.out.println("selling for amount " + amount);
        return amount;
    }


}
