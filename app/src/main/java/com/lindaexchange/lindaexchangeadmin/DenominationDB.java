package com.lindaexchange.lindaexchangeadmin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milion on 11/3/2017.
 */

public class DenominationDB {
    private String key;
    private String denominationname;
    private List<RateDB> denominationrate;

    public DenominationDB() {}

    public DenominationDB(String name) {
        this.key = "";
        this.denominationname = name;
        this.denominationrate = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDenominationname() {
        return denominationname;
    }

    public List<RateDB> getDenominationrate() {
        return denominationrate;
    }

    public void setDenominationrate(List<RateDB> denominationrate) {
        this.denominationrate = denominationrate;
    }
}
