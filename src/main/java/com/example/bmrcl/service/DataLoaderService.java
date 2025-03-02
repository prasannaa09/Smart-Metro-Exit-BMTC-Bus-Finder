package com.example.bmrcl.service;

import com.example.bmrcl.entity.BusStop;
import com.example.bmrcl.entity.MetroStation;
import com.example.bmrcl.repository.BusStopRepository;
import com.example.bmrcl.repository.MetroStationRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    @Autowired
    private MetroStationRepository metroStationRepository;

    @Autowired
    private BusStopRepository busStopRepository;

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
                    if (parts.length < 4) {
                        logger.warn("Skipping invalid row: {}", (Object) parts);
                        continue;
                    }
                    try {
                        MetroStation station = new MetroStation(
                                Long.parseLong(parts[0]),
                                parts[1],
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
                    if (parts.length < 9) {
                        logger.warn("Skipping invalid row: {}", (Object) parts);
                        continue;
                    }
                    try {
                        BusStop stop = new BusStop(
                                Long.parseLong(parts[4]),
                                parts[8],
                                Double.parseDouble(parts[5]),
                                Double.parseDouble(parts[6])
                        );
                        stops.add(stop);
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number format in row: {}", (Object) parts, e);
                    }
                }
                busStopRepository.saveAll(stops); // Batch insert
                logger.info("Successfully loaded {} bus stops", stops.size());
            }
        } catch (IOException | CsvException e) {
            logger.error("Failed to load bus stops", e);
        }
    }
}
