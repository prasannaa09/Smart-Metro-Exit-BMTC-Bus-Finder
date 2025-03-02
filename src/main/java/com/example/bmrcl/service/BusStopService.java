package com.example.bmrcl.service;

import com.example.bmrcl.entity.BusStop;
import com.example.bmrcl.repository.BusStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
class BusStopService {
    @Autowired
    private BusStopRepository busStopRepository;

    public BusStop findNearestBusStop(double metroLat, double metroLon) {
        return busStopRepository.findAll().stream()
                .min(Comparator.comparingDouble(b -> calculateDistance(metroLat, metroLon, b.getLatitude(), b.getLongitude())))
                .orElse(null);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}

