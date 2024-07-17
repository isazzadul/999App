package com.aibrains.emergency.models;

public class HelplineService {
    public String name;
    public String mobile;
    public String city;
    public String address;

    public HelplineService() {
    }

    public HelplineService(String name, String mobile, String city, String address) {
        this.name = name;
        this.mobile = mobile;
        this.city = city;
        this.address = address;
    }
}
