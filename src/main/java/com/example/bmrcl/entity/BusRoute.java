package com.example.bmrcl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class BusRoute {
    @Id
    private String id;
    private String name;

    @ManyToOne
    private MetroStation startStop;

    @ManyToOne
    private MetroStation endStop;
}