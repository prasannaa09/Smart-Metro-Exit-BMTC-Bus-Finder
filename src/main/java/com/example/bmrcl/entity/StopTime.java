package com.example.bmrcl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StopTime {
    @JsonProperty("trip_id")
    private String tripId;

    @JsonProperty("stop_id")
    private String stopId;

    // Default constructor
    public StopTime() {
    }

    // Constructor for BMTC stop times (stop_times.txt)
    public StopTime(String[] data) {
        this.tripId = data[9]; // trip_id is at index 9
        this.stopId = data[6]; // stop_id is at index 6
    }

    // Getters and Setters
    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }
}