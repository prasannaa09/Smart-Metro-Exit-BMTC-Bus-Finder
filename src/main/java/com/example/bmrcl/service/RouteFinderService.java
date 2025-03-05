
package com.example.bmrcl.service;

import com.example.bmrcl.entity.*;
import com.example.bmrcl.repository.GtfsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteFinderService {

    @Autowired
    private GtfsRepository gtfsRepository;

    public List<RouteOption> findRoutes(String metroStation, String destination) {
        Stop metroStop = findStopByName(gtfsRepository.getBmrclStops(), metroStation);
        if (metroStop == null) return List.of(new RouteOption("Metro station not found."));

        Stop destStop = findStopByName(gtfsRepository.getBmtcStops(), destination);
        if (destStop == null) return List.of(new RouteOption("Destination stop not found."));

        List<Stop> nearbyStops = findNearbyStops(metroStop, gtfsRepository.getBmtcStops());
        if (nearbyStops.isEmpty()) return List.of(new RouteOption("No nearby BMTC stops found."));

        return buildRouteOptions(nearbyStops, destStop);
    }

    private Stop findStopByName(List<Stop> stops, String name) {
        return stops.stream().filter(s -> s.getStopName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private List<Stop> findNearbyStops(Stop metroStop, List<Stop> bmtcStops) {
        return bmtcStops.stream()
                .filter(s -> calculateDistance(metroStop.getStopLat(), metroStop.getStopLon(), s.getStopLat(), s.getStopLon()) <= 2.0)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371.0 * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    private List<RouteOption> buildRouteOptions(List<Stop> nearbyStops, Stop destStop) {
        Map<String, List<StopTime>> tripToStops = gtfsRepository.getTripToStops();
        Map<String, String> tripToRoute = gtfsRepository.getTripToRoute();
        Map<String, String> routeToName = gtfsRepository.getRouteToName();

        List<RouteOption> options = new ArrayList<>();
        for (Stop start : nearbyStops) {
            for (Map.Entry<String, List<StopTime>> entry : tripToStops.entrySet()) {
                String tripId = entry.getKey();
                List<StopTime> stops = entry.getValue();
                if (stops.stream().anyMatch(st -> st.getStopId().equals(start.getStopId())) && stops.stream().anyMatch(st -> st.getStopId().equals(destStop.getStopId()))) {
                    String routeName = routeToName.get(tripToRoute.get(tripId));
                    options.add(new RouteOption("Take bus " + routeName + " from " + start.getStopName() + " to " + destStop.getStopName()));
                }
            }
        }
        return options.isEmpty() ? List.of(new RouteOption("No direct buses found.")) : options;
    }
}
