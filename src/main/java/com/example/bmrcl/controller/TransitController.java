
package com.example.bmrcl.controller;

import com.example.bmrcl.entity.RouteOption;
import com.example.bmrcl.service.RouteFinderService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransitController {

    @Autowired
    private RouteFinderService routeFinderService;

    @GetMapping("/routes")
    public ResponseEntity<List<RouteOption>> getBusRoutes(
            @RequestParam("metroStation") @NotBlank String metroStation,
            @RequestParam("destination") @NotBlank String destination) {
        try {
            return ResponseEntity.ok(routeFinderService.findRoutes(metroStation, destination));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(new RouteOption("Unexpected error occurred.")));
        }
    }
}
