package com.example.bmrcl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Trip {
    @JsonProperty("route_id")
    private String routeId;

    @JsonProperty("trip_id")
    private String tripId;

    // Default constructor
    public Trip() {
    }

    // Constructor for BMTC trips (trips.txt)
    public Trip(String[] data) {
        this.routeId = data[3]; // route_id is at index 3
        this.tripId = data[7]; // trip_id is at index 7
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}