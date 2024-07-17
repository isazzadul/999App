package com.aibrains.emergency.models;

public class PhoneList {
    public String model , userNID , color, status , imei;

    public PhoneList(String model, String userNID, String color, String status, String imei) {
        this.model = model;
        this.userNID = userNID;
        this.color = color;
        this.status = status;
        this.imei = imei ;
    }

    public PhoneList() {
    }
}
