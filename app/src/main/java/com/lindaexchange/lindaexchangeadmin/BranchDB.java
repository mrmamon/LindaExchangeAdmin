package com.lindaexchange.lindaexchangeadmin;

/**
 * Created by Milion on 9/3/2017.
 */

public class BranchDB {
    private String key;
    private String name;
    private String address;
    private String location;
    private String openingtime;
    private String contactnumber;
    private String photo;

    public BranchDB () {}

    public BranchDB(String key, String name, String address, String location, String time, String contact, String imageUrl) {
        this.key = key;
        this.name = name;
        this.location = location;
        this.openingtime = time;
        this.contactnumber = contact;
        this.photo = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public String getOpeningtime() {
        return openingtime;
    }

    public String getContactnumber() {
        return contactnumber;
    }

    public double getLat() {
        String latString = this.location.split(",")[0];
        return Double.parseDouble(latString);
    }

    public double getLng() {
        String[] splitLocation = this.location.split(",");
        if (splitLocation.length > 0) {
            return Double.parseDouble(splitLocation[1]);
        }
        return 0.0;
    }

    public String getPhoto() {
        return photo;
    }
}
