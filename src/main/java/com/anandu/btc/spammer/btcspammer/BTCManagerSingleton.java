package com.anandu.btc.spammer.btcspammer;

public class BTCManagerSingleton {

    private static BTCManagerSingleton INSTANCE = null;

    public double accountBalance = 0;
    public double numberOfCoins = 0;
    public double currentPrice = 0;

    private BTCManagerSingleton() {
    }

    public static BTCManagerSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BTCManagerSingleton();
        }
        return INSTANCE;
    }
}
