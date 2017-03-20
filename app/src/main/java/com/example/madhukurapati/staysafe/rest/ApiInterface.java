package com.example.madhukurapati.staysafe.rest;

/**
 * Created by kurapma on 1/19/17.
 */
import com.example.madhukurapati.staysafe.models.LocationResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {

    @POST("geolocate")
    Call<LocationResponse> getLocation(@Query("key") String key);

}
