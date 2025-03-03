package com.example.bmrcl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Route {
    @JsonProperty("route_id")
    private String routeId;

    @JsonProperty("route_long_name")
    private String routeLongName;

    // Default constructor
    public Route() {
    }

    // Constructor for BMTC routes (routes.txt)
    public Route(String[] data) {
        this.routeId = data[3]; // route_id is at index 3
        this.routeLongName = data[4].replace("\"", ""); // route_long_name is at index 4
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }
}