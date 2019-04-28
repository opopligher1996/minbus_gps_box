package com.example.cheukleong.minibus_project;

public class Station {

    StationLocation location =  new StationLocation( 0.0, 0.0);

    String staton_name = "";

    public Station(String staton_name, double lat, double lng){
        this.location = new StationLocation(lat,lng);
        this.staton_name = staton_name;
    }

    public double getDistance(double compare_lat, double compare_lng){
        return location.getDistance(compare_lat, compare_lng);
    }
}
