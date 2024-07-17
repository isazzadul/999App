package com.aibrains.emergency.models;

public class LostPhoneActivity {
    public String SearchBy , UserEmail , User_Name , User_Phone , Time ;

    public LostPhoneActivity() {
    }

    public LostPhoneActivity(String searchBy, String userEmail, String user_Name, String user_Phone, String time) {
        SearchBy = searchBy;
        UserEmail = userEmail;
        User_Name = user_Name;
        User_Phone = user_Phone;
        Time = time;
    }
}
