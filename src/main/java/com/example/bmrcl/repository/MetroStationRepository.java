package com.example.bmrcl.repository;


import com.example.bmrcl.entity.MetroStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroStationRepository extends JpaRepository<MetroStation, Long> {}