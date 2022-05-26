package com.anandu.btc.spammer.btcspammer.controller;

import DTO.BtcPrice;
import com.anandu.btc.spammer.btcspammer.BTCManagerSingleton;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

import static com.anandu.btc.spammer.btcspammer.Constants.*;

@RestController
public class GetDataController {


    @GetMapping("/price")
    String getPrice() {
        return String.format("Current price: %f <br/>number of coins: %f <br/>Account Balance: %f <br/> portfolio: %f",
                BTCManagerSingleton.getInstance().currentPrice, BTCManagerSingleton.getInstance().numberOfCoins, BTCManagerSingleton.getInstance().accountBalance,
                BTCManagerSingleton.getInstance().currentPrice * BTCManagerSingleton.getInstance().numberOfCoins + BTCManagerSingleton.getInstance().accountBalance);
    }

    @GetMapping("/")
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", prankerLocation);
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/btcprice")
    public String getBtcPrice() {

        String strLine = "";
        ArrayList<BtcPrice> list = new ArrayList<>();
        int noOfLines = 3600*2;
        Gson gson = new Gson();
        //Read File Line By Line

        try {
            FileInputStream fstream = new FileInputStream(priceList);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            while (true) {
                if (!((strLine = br.readLine()) != null))
                    break;
                list.add(gson.fromJson(strLine, BtcPrice.class));
            }
            fstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gson.toJson(list.subList(((list.size() - noOfLines) < 0) ? 0 : (list.size() - noOfLines), list.size()));
    }

}
