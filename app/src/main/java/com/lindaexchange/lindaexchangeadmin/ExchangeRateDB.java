package com.lindaexchange.lindaexchangeadmin;

import java.util.ArrayList;
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

    public List<RateValueDB> extractRateValue(int branch) {
        List<RateValueDB> array = new ArrayList<>();
        int denominationIndex = 0;
        for (DenominationDB deno : rate) {
            List<RateDB> rateDBList = deno.getDenominationrate();
            if (rateDBList != null && branch < rateDBList.size()) {
                RateDB rateDB = rateDBList.get(branch);
                array.add(new RateValueDB(key, countryname, currencyname,
                        String.valueOf(denominationIndex), deno.getDenominationname(),
                        String.valueOf(branch),
                        rateDB.getBuy(), rateDB.getSell()));
            } else {
                array.add(new RateValueDB(key, countryname, currencyname,
                        String.valueOf(denominationIndex), deno.getDenominationname(),
                        String.valueOf(branch),
                        "0.00", "0.00"));
            }
            denominationIndex += 1;
        }
        return array;
    }
}