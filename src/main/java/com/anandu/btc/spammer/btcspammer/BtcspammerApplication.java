package com.anandu.btc.spammer.btcspammer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static com.anandu.btc.spammer.btcspammer.Utils.*;

@SpringBootApplication
public class BtcspammerApplication {


    public static void main(String[] args) {

        String filename = "priceList.txt";
        String tradingReportfilename = "tradeReport.txt";

        float price = 0F, old1 = 0F, old2 = 0F, old3 = 0F;
        int confidence;
        boolean startTrading = false; // change to true when ready to buy and sell
        float accountBalance = 1000;

        SpringApplication.run(BtcspammerApplication.class, args);

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

            sleep(1);
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
                                    timestamp));

            if (startTrading) { // if it is ok to trade
                if (confidence > 30) { // if confidence is good
                    if (accountBalance > 0) {// and if there is still cash in account
                        accountBalance -= buyFor(accountBalance); // reduce account balance with the bought amount
                        writeToFile(tradingReportfilename, String.valueOf(price));
                    }
                }
                if (confidence < 30) {
                    if (accountBalance < 1000) { //TODO:change this according to API. sell only if anything is there to sell
                        accountBalance += sellFor(accountBalance);
                        writeToFile(tradingReportfilename, "-" + price);
                    }
                }
            }


        }
    }


}
