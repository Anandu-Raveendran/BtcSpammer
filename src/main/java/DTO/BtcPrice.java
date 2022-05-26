package DTO;

import java.sql.Timestamp;

public class BtcPrice {
    public Double price;
    public int confidence;
    public Timestamp time;

    public BtcPrice(Double price, int confidence, Timestamp time) {
        this.price = price;
        this.confidence = confidence;
        this.time = time;
    }
}
