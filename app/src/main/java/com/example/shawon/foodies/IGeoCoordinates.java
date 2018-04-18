package com.example.shawon.foodies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SHAWON on 3/15/2018.
 */

public interface IGeoCoordinates {

    @GET("maps/api/geocode/json") // To access the Google Maps Geocoding API over HTTP, use

    Call<String> getGeoCode(@Query("address") String address); // means maps/api/geocode/json?address=Noakhali

    @GET("maps/api/directions/json")

    Call<String> getDirections(@Query("origin") String origin,@Query("destination") String destination);

}
