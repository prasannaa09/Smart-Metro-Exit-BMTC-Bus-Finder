package com.example.bmrcl.controller;

import com.example.bmrcl.entity.RouteOption;
import com.example.bmrcl.exception.TransitException;
import com.example.bmrcl.service.RouteFinderService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for transit route requests.
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TransitController {

    @Autowired
    private RouteFinderService routeFinderService;

    @GetMapping("/routes")
    public ResponseEntity<List<RouteOption>> getBusRoutes(
            @RequestParam(value = "metroStation", required = true) @NotBlank(message = "Metro station is required") String metroStation,
            @RequestParam(value = "destination", required = true) @NotBlank(message = "Destination is required") String destination) {
        try {
            List<RouteOption> routes = routeFinderService.findBusRoutes(metroStation, destination);
            return ResponseEntity.ok(routes);
        } catch (TransitException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(List.of(new RouteOption("Error: " + e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(new RouteOption("Unexpected server error occurred")));
        }
    }

    @ExceptionHandler(TransitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<RouteOption>> handleTransitException(TransitException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(List.of(new RouteOption("Error: " + e.getMessage())));
    }
}