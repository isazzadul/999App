package com.aibrains.emergency;

public class MyLocation {
    public double latitude;
    public double longitude;

    public double getLatitude() {
        return latitude;
    }

    public MyLocation() {
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public MyLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
