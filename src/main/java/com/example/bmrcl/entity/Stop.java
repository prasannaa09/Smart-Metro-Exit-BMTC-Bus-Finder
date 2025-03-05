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

    public Stop() {}

    public Stop(String[] data, boolean isBmrc) {
        try {
            if (isBmrc && data.length > 3) {
                this.stopId = data[0].trim();
                this.stopName = data[1].replace("\"", "").trim();
                this.stopLat = Double.parseDouble(data[2].trim());
                this.stopLon = Double.parseDouble(data[3].trim());
            } else if (data.length > 7) {
                this.stopId = data[4].trim();
                this.stopName = data[7].replace("\"", "").trim();
                this.stopLat = Double.parseDouble(data[5].trim());
                this.stopLon = Double.parseDouble(data[6].trim());
            }
        } catch (Exception e) {
            System.err.println("Error parsing stop data: " + String.join(",", data));
        }
    }

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