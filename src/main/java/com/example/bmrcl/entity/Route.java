package com.example.bmrcl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Route {
    @JsonProperty("route_id")
    private String routeId;

    @JsonProperty("route_long_name")
    private String routeLongName;

    public Route() {}

    public Route(String[] data) {
        if (data.length > 4) {
            this.routeId = data[3].trim();
            this.routeLongName = data[4].replace("\"", "").trim();
        }
    }

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
