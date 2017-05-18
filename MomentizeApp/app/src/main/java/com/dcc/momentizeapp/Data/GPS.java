package com.dcc.momentizeapp.Data;

/**
 * Created by MorcosS on 5/17/17.
 */

public class GPS {
    double lat, lon;

    public GPS(){

    }

    public GPS(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
