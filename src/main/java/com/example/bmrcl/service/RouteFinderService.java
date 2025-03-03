package com.example.bmrcl.service;

import com.example.bmrcl.entity.*;
import com.example.bmrcl.exception.TransitException;
import com.example.bmrcl.repository.GtfsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to find BMTC bus routes connecting BMRCL metro stations to destinations.
 */
@Service
public class RouteFinderService {

    @Autowired
    private GtfsRepository gtfsRepository;

    private static final double MAX_WALKING_DISTANCE_KM = 1.0;

    public List<RouteOption> findBusRoutes(String metroStation, String destination) {
        if (metroStation == null || metroStation.trim().isEmpty() || destination == null || destination.trim().isEmpty()) {
            throw new TransitException("Metro station and destination must not be empty");
        }

        try {
            List<Stop> bmrcStops = gtfsRepository.getBmrcStops();
            List<Stop> bmtcStops = gtfsRepository.getBmtcStops();
            List<Route> bmtcRoutes = gtfsRepository.getBmtcRoutes();
            List<Trip> bmtcTrips = gtfsRepository.getBmtcTrips();
            List<StopTime> bmtcStopTimes = gtfsRepository.getBmtcStopTimes();

            Stop metroStop = findStopByName(bmrcStops, metroStation);
            if (metroStop == null) {
                return List.of(new RouteOption("Error: Metro station '" + metroStation + "' not found"));
            }

            Stop destStop = findStopByName(bmtcStops, destination);
            if (destStop == null) {
                return List.of(new RouteOption("Error: Destination '" + destination + "' not found"));
            }

            List<Stop> nearbyStops = findNearbyStops(bmtcStops, metroStop);
            if (nearbyStops.isEmpty()) {
                return List.of(new RouteOption("No bus stops found within " + MAX_WALKING_DISTANCE_KM + " km of " + metroStation));
            }

            List<RouteOption> options = buildRouteOptions(nearbyStops, destStop, bmtcTrips, bmtcStopTimes, bmtcRoutes);
            return options.isEmpty() ?
                    List.of(new RouteOption("No direct bus routes found from " + metroStation + " to " + destination)) :
                    options;

        } catch (TransitException e) {
            throw e;
        } catch (Exception e) {
            throw new TransitException("Unexpected error finding bus routes", e);
        }
    }

    private Stop findStopByName(List<Stop> stops, String name) {
        return stops.stream()
                .filter(stop -> stop.getStopName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private List<Stop> findNearbyStops(List<Stop> bmtcStops, Stop metroStop) {
        return bmtcStops.stream()
                .filter(stop -> calculateHaversineDistance(
                        metroStop.getStopLat(), metroStop.getStopLon(),
                        stop.getStopLat(), stop.getStopLon()) <= MAX_WALKING_DISTANCE_KM)
                .collect(Collectors.toList());
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        try {
            final int R = 6371; // Earth radius in kilometers
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
        } catch (ArithmeticException e) {
            throw new TransitException("Error calculating distance between coordinates", e);
        }
    }

    private List<RouteOption> buildRouteOptions(List<Stop> nearbyStops, Stop destStop,
                                                List<Trip> trips, List<StopTime> stopTimes,
                                                List<Route> routes) {
        try {
            Map<String, List<String>> tripToStops = stopTimes.stream()
                    .collect(Collectors.groupingBy(
                            StopTime::getTripId,
                            Collectors.mapping(StopTime::getStopId, Collectors.toList())
                    ));

            Map<String, String> tripToRoute = trips.stream()
                    .collect(Collectors.toMap(Trip::getTripId, Trip::getRouteId));

            Map<String, String> routeToName = routes.stream()
                    .collect(Collectors.toMap(Route::getRouteId, Route::getRouteLongName));

            List<RouteOption> options = new ArrayList<>();
            Set<String> processedRoutes = new HashSet<>();

            for (Stop nearbyStop : nearbyStops) {
                for (Map.Entry<String, List<String>> entry : tripToStops.entrySet()) {
                    String tripId = entry.getKey();
                    List<String> stops = entry.getValue();

                    if (stops.contains(nearbyStop.getStopId()) && stops.contains(destStop.getStopId())) {
                        String routeId = tripToRoute.get(tripId);
                        if (routeId == null) continue;

                        String routeName = routeToName.get(routeId);
                        if (routeName == null || processedRoutes.contains(routeId)) continue;

                        double distance = calculateHaversineDistance(
                                nearbyStop.getStopLat(), nearbyStop.getStopLon(),
                                destStop.getStopLat(), destStop.getStopLon()
                        );
                        options.add(new RouteOption(routeName, nearbyStop.getStopName(),
                                destStop.getStopName(), distance));
                        processedRoutes.add(routeId);
                    }
                }
            }
            return options;
        } catch (Exception e) {
            throw new TransitException("Error building route options", e);
        }
    }
}