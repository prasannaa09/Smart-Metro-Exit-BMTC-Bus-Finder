package com.example.bmrcl.controller;

import com.example.bmrcl.service.BusStopService;
import com.example.bmrcl.entity.BusRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class BusRouteController {

    @Autowired
    private BusStopService busStopService;

    /**
     * API endpoint to get bus routes based on metro station and desired location.
     * @param metroStationName the name of the metro station where the commuter arrives
     * @param desiredLocation the desired destination the commuter wants to reach
     * @return list of bus routes that can take the commuter from metro station to desired location
     */
    @GetMapping("/find")
    public ResponseEntity<List<BusRoute>> getBusRoutes(@RequestParam String metroStationName, @RequestParam String desiredLocation) {
        List<BusRoute> busRoutes = busStopService.findBusRoutesToDestination(metroStationName, desiredLocation);

        if (busRoutes == null || busRoutes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(busRoutes);
    }
}
