package com.example.routing_backend.service;

import com.example.routing_backend.entity.Vehicle;

import com.example.routing_backend.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
}
