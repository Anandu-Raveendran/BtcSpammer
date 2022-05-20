package com.anandu.btc.spammer.btcspammer.controller;

import com.anandu.btc.spammer.btcspammer.BTCManagerSingleton;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

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
        httpServletResponse.setHeader("Location", "https://anandu-raveendran.github.io/react-pranker/");
        httpServletResponse.setStatus(302);
    }

}
