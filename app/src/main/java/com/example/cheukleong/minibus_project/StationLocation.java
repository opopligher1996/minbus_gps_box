package com.example.cheukleong.minibus_project;

import android.location.Location;

public class StationLocation {

    double lat = 0.0;
    double lng = 0.0;

    public StationLocation(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getDistance(double compare_lat, double compare_lng){
        double distance = 0.0;
        Location compare_location = new Location("compare_location");
        Location location = new Location("location");
        compare_location.setLatitude(compare_lat);
        compare_location.setLongitude(compare_lng);
        location.setLatitude(this.lat);
        location.setLongitude(this.lng);
        distance = location.distanceTo(compare_location);
        return distance;
    }
}
