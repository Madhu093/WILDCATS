package com.example.madhukurapati.staysafe.models;

import retrofit2.http.PUT;

/**
 * Created by madhukurapati on 3/20/17.
 */

public class LocationCityAndState {
    public String city;

    public String state;

    public LocationCityAndState() {
        // Default constructor required for calls to DataSnapshot.getValue(LocationCityAndState.class)
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
