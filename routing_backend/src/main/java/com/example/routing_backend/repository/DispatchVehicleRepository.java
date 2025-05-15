package com.example.routing_backend.repository;

import com.example.routing_backend.entity.DispatchVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchVehicleRepository extends JpaRepository<DispatchVehicle, Long> {
    DispatchVehicle findByVehicleId(Long id);
}