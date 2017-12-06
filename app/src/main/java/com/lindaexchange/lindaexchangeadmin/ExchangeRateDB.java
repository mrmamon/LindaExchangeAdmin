package com.lindaexchange.lindaexchangeadmin;

import java.util.List;

/**
 * Created by Milion on 11/3/2017.
 */

public class ExchangeRateDB {
    private String key;
    private String countryname;
    private String currencyname;
    private String flag;
    private List<DenominationDB> rate;

    public ExchangeRateDB() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCountryname() {
        return countryname;
    }

    public String getCurrencyname() {
        return currencyname;
    }

    public String getFlag() {
        return flag;
    }

    public List<DenominationDB> getRate() {
        return rate;
    }

    public void setRate(List<DenominationDB> rate) {
        this.rate = rate;
    }
}