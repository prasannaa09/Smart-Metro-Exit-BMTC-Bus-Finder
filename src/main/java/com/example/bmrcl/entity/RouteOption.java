package com.example.bmrcl.entity;

public class RouteOption {
    private String routeName;
    private String boardingStop;
    private String alightingStop;
    private double distanceKm;

    // Default constructor
    public RouteOption() {
    }

    // Constructor for error messages
    public RouteOption(String routeName) {
        this.routeName = routeName;
    }

    // Full constructor
    public RouteOption(String routeName, String boardingStop, String alightingStop, double distanceKm) {
        this.routeName = routeName;
        this.boardingStop = boardingStop;
        this.alightingStop = alightingStop;
        this.distanceKm = distanceKm;
    }

    // Getters and Setters
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getBoardingStop() {
        return boardingStop;
    }

    public void setBoardingStop(String boardingStop) {
        this.boardingStop = boardingStop;
    }

    public String getAlightingStop() {
        return alightingStop;
    }

    public void setAlightingStop(String alightingStop) {
        this.alightingStop = alightingStop;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }
}