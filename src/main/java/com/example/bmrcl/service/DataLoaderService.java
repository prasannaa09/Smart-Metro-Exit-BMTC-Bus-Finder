package com.example.bmrcl.service;

import com.example.bmrcl.entity.BusStop;
import com.example.bmrcl.entity.MetroStation;
import com.example.bmrcl.repository.BusStopRepository;
import com.example.bmrcl.repository.MetroStationRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    @Autowired
    private MetroStationRepository metroStationRepository;

    @Autowired
    private BusStopRepository busStopRepository;

    // Load Metro Stations
    @Transactional
    public void loadMetroStations() {
        List<MetroStation> stations = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("bmrcl/stops.txt")) {
            if (inputStream == null) {
                logger.error("Metro stations file not found");
                return;
            }

            try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                List<String[]> lines = reader.readAll();
                if (lines.size() <= 1) {
                    logger.warn("Metro stations file is empty or has only headers");
                    return;
                }
                lines.remove(0); // Remove header

                for (String[] parts : lines) {
                    if (parts.length < 4 || isRowEmpty(parts)) {
                        logger.warn("Skipping invalid or empty row: {}", (Object) parts);
                        continue;
                    }
                    try {
                        Long id = Long.parseLong(parts[0]);
                        String name = parts[1];

                        // Skip if name is empty or null
                        if (name == null || name.trim().isEmpty()) {
                            logger.warn("Skipping Metro Station with empty name, ID: {}", id);
                            continue;
                        }

                        // Check if this station already exists
                        Optional<MetroStation> existingStation = metroStationRepository.findById(id);
                        if (existingStation.isPresent()) {
                            logger.info("Metro Station {} already exists, skipping.", name);
                            continue;
                        }

                        MetroStation station = new MetroStation(
                                id,
                                name,
                                Double.parseDouble(parts[2]),
                                Double.parseDouble(parts[3])
                        );
                        stations.add(station);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number format in row: {}", (Object) parts, e);
                    }
                }
                metroStationRepository.saveAll(stations); // Batch insert
                logger.info("Successfully loaded {} metro stations", stations.size());
            }
        } catch (IOException | CsvException e) {
            logger.error("Failed to load metro stations", e);
        }
    }

    // Load Bus Stops
    @Transactional
    public void loadBusStops() {
        List<BusStop> stops = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("bmtc/stops.txt")) {
            if (inputStream == null) {
                logger.error("Bus stops file not found");
                return;
            }

            try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                List<String[]> lines = reader.readAll();
                if (lines.size() <= 1) {
                    logger.warn("Bus stops file is empty or has only headers");
                    return;
                }
                lines.remove(0); // Remove header

                for (String[] parts : lines) {
                    if (parts.length < 9 || isRowEmpty(parts)) {
                        logger.warn("Skipping invalid or empty row: {}", (Object) parts);
                        continue;
                    }
                    try {
                        Long id = Long.parseLong(parts[4]);
                        String name = parts[7]; // Bus stop name is in the 8th column (index 7)

                        // Skip if name is empty or null
                        if (name == null || name.trim().isEmpty()) {
                            logger.warn("Skipping Bus Stop with empty name, ID: {}", id);
                            continue;
                        }

                        // Check if this bus stop already exists
                        Optional<BusStop> existingBusStop = busStopRepository.findById(id);
                        if (existingBusStop.isPresent()) {
                            logger.info("Bus Stop {} already exists, skipping.", name);
                            continue;
                        }

                        BusStop stop = new BusStop(
                                id,
                                name,
                                Double.parseDouble(parts[5]), // Latitude at index 5
                                Double.parseDouble(parts[6])  // Longitude at index 6
                        );
                        stops.add(stop);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number format in row: {}", (Object) parts, e);
                    }
                }

                // Save the stops in batches
                saveInBatches(stops, 1000); // Adjust batch size as needed
                logger.info("Successfully loaded {} bus stops", stops.size());
            }
        } catch (IOException | CsvException e) {
            logger.error("Failed to load bus stops", e);
        }
    }

    // Helper method to save in batches
    private void saveInBatches(List<BusStop> stops, int batchSize) {
        int totalStops = stops.size();
        for (int i = 0; i < totalStops; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalStops);
            List<BusStop> batch = stops.subList(i, endIndex);
            busStopRepository.saveAll(batch);
            logger.info("Successfully saved batch {}-{}", i, endIndex);
        }
    }

    // Helper method to check if a row is empty (i.e., all fields are null or empty)
    private boolean isRowEmpty(String[] row) {
        for (String column : row) {
            if (column != null && !column.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
