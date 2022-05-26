package com.anandu.btc.spammer.btcspammer.controller;

import com.anandu.btc.spammer.btcspammer.BTCManagerSingleton;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static com.anandu.btc.spammer.btcspammer.Constants.*;

@RestController
public class GetDataController {

    private FileInputStream fstream;
    private BufferedReader br;

    public GetDataController() {
        super();
        {
            try {
                fstream = new FileInputStream(priceList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        {
            br = new BufferedReader(new InputStreamReader(fstream));
        }

    }

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

    @GetMapping("/btcprice")
    public String getBtcPrice() {

        String strLine = "";

        //Read File Line By Line
        while (true) {
            try {
                if (!((strLine = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Print the content on the console
            System.out.println(strLine);
        }


        //Close the input stream
        return tokens[1];
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fstream.close();
    }
}
