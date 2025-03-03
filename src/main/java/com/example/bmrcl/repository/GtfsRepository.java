package com.example.bmrcl.repository;


import com.example.bmrcl.entity.Route;
import com.example.bmrcl.entity.Stop;
import com.example.bmrcl.entity.StopTime;
import com.example.bmrcl.entity.Trip;
import com.example.bmrcl.exception.TransitException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for fetching GTFS data from GitHub.
 */
@Repository
public class GtfsRepository {

    // Replace with your GitHub repository raw URLs
    private static final String BMTC_ROUTES_URL = "https://raw.githubusercontent.com/yourusername/bmtc-gtfs/main/routes.txt";
    private static final String BMTC_STOPS_URL = "https://raw.githubusercontent.com/yourusername/bmtc-gtfs/main/stops.txt";
    private static final String BMTC_TRIPS_URL = "https://raw.githubusercontent.com/yourusername/bmtc-gtfs/main/trips.txt";
    private static final String BMTC_STOP_TIMES_URL = "https://raw.githubusercontent.com/yourusername/bmtc-gtfs/main/stop_times.txt";
    private static final String BMRCL_STOPS_URL = "https://raw.githubusercontent.com/yourusername/bmrc-gtfs/main/stopsbmrcl.txt";

    public List<Stop> getBmrcStops() {
        try {
            return fetchCsvData(BMRCL_STOPS_URL).stream()
                    .map(data -> new Stop(data, true)) // BMRCL stops
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TransitException("Failed to fetch BMRCL stops", e);
        }
    }

    public List<Route> getBmtcRoutes() {
        try {
            return fetchCsvData(BMTC_ROUTES_URL).stream()
                    .map(Route::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TransitException("Failed to fetch BMTC routes", e);
        }
    }

    public List<Stop> getBmtcStops() {
        try {
            return fetchCsvData(BMTC_STOPS_URL).stream()
                    .map(data -> new Stop(data, false)) // BMTC stops
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TransitException("Failed to fetch BMTC stops", e);
        }
    }

    public List<Trip> getBmtcTrips() {
        try {
            return fetchCsvData(BMTC_TRIPS_URL).stream()
                    .map(Trip::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TransitException("Failed to fetch BMTC trips", e);
        }
    }

    public List<StopTime> getBmtcStopTimes() {
        try {
            return fetchCsvData(BMTC_STOP_TIMES_URL).stream()
                    .map(StopTime::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new TransitException("Failed to fetch BMTC stop times", e);
        }
    }

    private List<String[]> fetchCsvData(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            return client.execute(request, response -> {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new TransitException("Failed to fetch data from " + url + ": HTTP " + response.getStatusLine().getStatusCode());
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                reader.readLine(); // Skip header
                return reader.lines()
                        .map(line -> line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")) // Handle commas within quotes
                        .collect(Collectors.toList());
            });
        } catch (Exception e) {
            throw new TransitException("Error fetching GTFS data from " + url, e);
        }
    }

    private Stop createStop(String[] data) {
        try {
            return new Stop(data, false); // Default to BMTC format; adjust if needed
        } catch (NumberFormatException e) {
            throw new TransitException("Invalid coordinate format in stop data: " + String.join(",", data), e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TransitException("Malformed stop data: " + String.join(",", data), e);
        }
    }
}