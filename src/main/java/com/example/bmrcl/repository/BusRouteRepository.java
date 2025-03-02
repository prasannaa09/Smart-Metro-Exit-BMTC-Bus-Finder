package com.example.bmrcl.repository;


import com.example.bmrcl.entity.BusRoute;
import com.example.bmrcl.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {
    List<BusRoute> findByStartStop(BusStop startStop);
}

