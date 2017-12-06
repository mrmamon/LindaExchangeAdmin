package com.lindaexchange.lindaexchangeadmin;

/**
 * Created by Milion on 11/3/2017.
 */

public class RateDB {
    private String key;
    private String buy;
    private String sell;

    public RateDB() {}

    public RateDB(String buy, String sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }
}
