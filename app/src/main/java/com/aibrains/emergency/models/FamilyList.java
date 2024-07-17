package com.aibrains.emergency.models;

public class FamilyList {
    public String addedBy , userID , name;

    public FamilyList(String addedBy, String userID, String name) {
        this.addedBy = addedBy;
        this.userID = userID;
        this.name = name ;
    }

    public FamilyList() {
    }
}
