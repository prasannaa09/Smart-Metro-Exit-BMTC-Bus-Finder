package com.example.bmrcl.repository;

import com.example.bmrcl.entity.*;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GtfsRepository {

    private List<String[]> fetchCsvData(String url) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                List<String> fields = new ArrayList<>();
                boolean inQuotes = false;
                StringBuilder field = new StringBuilder();

                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        inQuotes = !inQuotes; // Toggle quote mode
                    } else if (c == ',' && !inQuotes) {
                        fields.add(field.toString().trim());
                        field.setLength(0); // Reset field
                    } else {
                        field.append(c);
                    }
                }
                fields.add(field.toString().trim()); // Add last field
                data.add(fields.toArray(new String[0]));
            }
        } catch (Exception e) {
            System.err.println("Error fetching data from: " + url + " - " + e.getMessage());
        }
        return data;
    }

    public List<Stop> getBmtcStops() {
        return fetchCsvData("https://raw.githubusercontent.com/prasannaa09/Smart-Metro-Exit-BMTC-Bus-Finder/master/src/main/resources/bmtc/stopsbmtc.txt").stream()
                .map(data -> new Stop(data, false)).collect(Collectors.toList());
    }

    public List<Stop> getBmrclStops() {
        List<Stop> stops = fetchCsvData("https://raw.githubusercontent.com/prasannaa09/Smart-Metro-Exit-BMTC-Bus-Finder/master/src/main/resources/bmrcl/stopsbmrcl.txt").stream()
                .map(data -> new Stop(data, true)).collect(Collectors.toList());
        System.out.println("Loaded " + stops.size() + " BMRCL stops."); // Debugging log
        return stops;
    }

    public List<Route> getBmtcRoutes() {
        return fetchCsvData("https://raw.githubusercontent.com/prasannaa09/Smart-Metro-Exit-BMTC-Bus-Finder/master/src/main/resources/bmtc/routes.txt").stream()
                .map(Route::new).collect(Collectors.toList());
    }

    public List<StopTime> getBmtcStopTimes() {
        return fetchCsvData("https://raw.githubusercontent.com/prasannaa09/Smart-Metro-Exit-BMTC-Bus-Finder/master/src/main/resources/bmtc/stop_times.txt").stream()
                .map(StopTime::new).collect(Collectors.toList());
    }

    public List<Trip> getBmtcTrips() {
        return fetchCsvData("https://raw.githubusercontent.com/prasannaa09/Smart-Metro-Exit-BMTC-Bus-Finder/master/src/main/resources/bmtc/trips.txt").stream()
                .map(Trip::new).collect(Collectors.toList());
    }

    public Map<String, List<StopTime>> getTripToStops() {
        List<StopTime> stopTimes = getBmtcStopTimes();
        return stopTimes.stream().collect(Collectors.groupingBy(StopTime::getTripId));
    }

    public Map<String, String> getTripToRoute() {
        List<Trip> trips = getBmtcTrips();
        return trips.stream().collect(Collectors.toMap(Trip::getTripId, Trip::getRouteId));
    }

    public Map<String, String> getRouteToName() {
        List<Route> routes = getBmtcRoutes();
        return routes.stream().collect(Collectors.toMap(Route::getRouteId, Route::getRouteLongName));
    }
}
