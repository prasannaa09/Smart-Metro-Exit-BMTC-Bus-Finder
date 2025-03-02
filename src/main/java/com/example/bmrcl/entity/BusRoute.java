package com.example.bmrcl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class BusRoute {
    @Id
    private String id;
    private String name;

    @ManyToOne
    private BusStop startStop;  // Changed from MetroStation to BusStop

    @ManyToOne
    private BusStop endStop;    // Changed from MetroStation to BusStop

    // Getter for startStop
    public BusStop getStartStop() {
        return startStop;
    }

    // Getter for endStop
    public BusStop getEndStop() {
        return endStop;
    }

    // Setter for startStop
    public void setStartStop(BusStop startStop) {
        this.startStop = startStop;
    }

    // Setter for endStop
    public void setEndStop(BusStop endStop) {
        this.endStop = endStop;
    }
}
