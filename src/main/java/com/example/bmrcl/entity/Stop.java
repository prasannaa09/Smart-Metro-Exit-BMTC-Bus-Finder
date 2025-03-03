package com.example.bmrcl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stop {
    @JsonProperty("stop_id")
    private String stopId;

    @JsonProperty("stop_name")
    private String stopName;

    @JsonProperty("stop_lat")
    private double stopLat;

    @JsonProperty("stop_lon")
    private double stopLon;

    // Default constructor
    public Stop() {
    }

    // Constructor for BMRCL stops (stopsbmrcl.txt)
    public Stop(String[] data, boolean isBmrc) {
        if (isBmrc) {
            this.stopId = data[0];
            this.stopName = data[1].replace("\"", ""); // Remove quotes if present
            this.stopLat = Double.parseDouble(data[2]);
            this.stopLon = Double.parseDouble(data[3]);
        } else {
            // Constructor for BMTC stops (stops.txt)
            this.stopId = data[4]; // stop_id is at index 4
            this.stopName = data[7].replace("\"", ""); // stop_name is at index 7
            this.stopLat = Double.parseDouble(data[5]); // stop_lat is at index 5
            this.stopLon = Double.parseDouble(data[6]); // stop_lon is at index 6
        }
    }

    // Constructor for generic use (assuming data array matches fields)
    public Stop(String[] data) {
        this.stopId = data[0];
        this.stopName = data[2].replace("\"", "");
        this.stopLat = Double.parseDouble(data[4]);
        this.stopLon = Double.parseDouble(data[5]);
    }

    // Getters and Setters
    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public double getStopLat() {
        return stopLat;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public double getStopLon() {
        return stopLon;
    }

    public void setStopLon(double stopLon) {
        this.stopLon = stopLon;
    }
}