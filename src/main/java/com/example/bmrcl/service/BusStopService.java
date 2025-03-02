package com.example.bmrcl.service;

import com.example.bmrcl.entity.BusRoute;
import com.example.bmrcl.entity.BusStop;
import com.example.bmrcl.entity.MetroStation;
import com.example.bmrcl.repository.BusRouteRepository;
import com.example.bmrcl.repository.BusStopRepository;
import com.example.bmrcl.repository.MetroStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusStopService {

    @Autowired
    private BusStopRepository busStopRepository;

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private MetroStationRepository metroStationRepository;

    /**
     * Find the bus routes from the nearest bus stop to the metro station to the desired location.
     * @param metroStationName the name of the metro station
     * @param desiredLocation the desired destination
     * @return the list of bus routes to the desired location
     */
    public List<BusRoute> findBusRoutesToDestination(String metroStationName, String desiredLocation) {
        // Step 1: Find the metro station by name
        Optional<MetroStation> metroStationOptional = metroStationRepository.findByName(metroStationName);

        if (metroStationOptional.isEmpty()) {
            return null; // Metro station not found
        }

        MetroStation metroStation = metroStationOptional.get();

        // Step 2: Find the nearest bus stop to the metro station
        BusStop nearestBusStop = findNearestBusStop(metroStation.getLatitude(), metroStation.getLongitude());

        if (nearestBusStop == null) {
            return null; // No bus stop found
        }

        // Step 3: Find bus routes that go through the nearest bus stop
        List<BusRoute> busRoutes = busRouteRepository.findByStartStop(nearestBusStop);

        // Step 4: Filter the bus routes that go to the desired location
        return busRoutes.stream()
                .filter(route -> route.getEndStop().getName().equalsIgnoreCase(desiredLocation))
                .collect(Collectors.toList());
    }

    /**
     * Find the nearest bus stop to the given latitude and longitude.
     * @param metroLat latitude of the metro station
     * @param metroLon longitude of the metro station
     * @return the nearest bus stop
     */
    private BusStop findNearestBusStop(double metroLat, double metroLon) {
        return busStopRepository.findAll().stream()
                .min((b1, b2) -> {
                    double distance1 = calculateDistance(metroLat, metroLon, b1.getLatitude(), b1.getLongitude());
                    double distance2 = calculateDistance(metroLat, metroLon, b2.getLatitude(), b2.getLongitude());
                    return Double.compare(distance1, distance2);
                })
                .orElse(null);
    }

    /**
     * Calculate the distance between two geographic coordinates using the Haversine formula.
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return the distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
