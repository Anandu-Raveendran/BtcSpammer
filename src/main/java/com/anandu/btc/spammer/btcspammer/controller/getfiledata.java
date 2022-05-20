package com.anandu.btc.spammer.btcspammer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class getfiledata {

    @GetMapping("/price")
    int getPrice() {
        return 4;
    }

    @GetMapping("/")
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "https://anandu-raveendran.github.io/react-pranker/");
        httpServletResponse.setStatus(302);
    }

}
