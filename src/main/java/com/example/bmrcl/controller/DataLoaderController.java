package com.example.bmrcl.controller;

import com.example.bmrcl.service.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/load-data")
class DataLoaderController {
    @Autowired
    private DataLoaderService dataLoaderService;

    @PostMapping("/metro-stations")
    public ResponseEntity<String> loadMetroStations() {
        try {
            dataLoaderService.loadMetroStations();
            return ResponseEntity.ok("Metro stations loaded successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/bus-stops")
    public ResponseEntity<String> loadBusStops() {
        try {
            dataLoaderService.loadBusStops();
            return ResponseEntity.ok("Bus stops loaded successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
