package com.lindaexchange.lindaexchangeadmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Milion on 12/8/2017.
 */

public class RateValueDB {
    private String rateIndex;
    private String countryname;
    private String currencyname;
    private String denominationIndex;
    private String denominationname;
    private String branchIndex;
    private String buy;
    private String sell;

    public RateValueDB(String rateIndex, String countryname, String currencyname, String denominationIndex, String denominationname, String branchIndex, String buy, String sell) {
        this.rateIndex = rateIndex;
        this.countryname = countryname;
        this.currencyname = currencyname;
        this.denominationIndex = denominationIndex;
        this.denominationname = denominationname;
        this.branchIndex = branchIndex;
        this.buy = buy;
        this.sell = sell;
    }

    public Map<String, Object> getMap() {
        String path = rateIndex + "/" + "rate" + "/" + denominationIndex + "/" + "denominationrate" + "/" + branchIndex + "/";
        Map<String, Object> map = new HashMap<>();
        map.put(path + "buy", buy);
        map.put(path + "sell" , sell);
        return map;
    }

    public String getRateIndex() {
        return rateIndex;
    }

    public void setRateIndex(String rateIndex) {
        this.rateIndex = rateIndex;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getCurrencyname() {
        return currencyname;
    }

    public void setCurrencyname(String currencyname) {
        this.currencyname = currencyname;
    }

    public String getDenominationIndex() {
        return denominationIndex;
    }

    public void setDenominationIndex(String denominationIndex) {
        this.denominationIndex = denominationIndex;
    }

    public String getDenominationname() {
        return denominationname;
    }

    public void setDenominationname(String denominationname) {
        this.denominationname = denominationname;
    }

    public String getBranchIndex() {
        return branchIndex;
    }

    public void setBranchIndex(String branchIndex) {
        this.branchIndex = branchIndex;
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
