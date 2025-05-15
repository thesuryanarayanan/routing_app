package com.example.routing_backend.service;

import com.example.routing_backend.entity.DispatchVehicle;

import com.example.routing_backend.repository.DispatchVehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchVehicleService {

    private final DispatchVehicleRepository dispatchVehicleRepository;

    public DispatchVehicleService(
            DispatchVehicleRepository dispatchVehicleRepository
    ) {
        this.dispatchVehicleRepository = dispatchVehicleRepository;
    }

    public List<DispatchVehicle> findAll() {
        return dispatchVehicleRepository.findAll();
    }

    public DispatchVehicle findById(Long id) {
        return dispatchVehicleRepository.findById(id).orElse(null);
    }

    public DispatchVehicle save(DispatchVehicle dispatchVehicle) {
        return dispatchVehicleRepository.save(dispatchVehicle);
    }

    public void deleteById(Long id) {
        dispatchVehicleRepository.deleteById(id);
    }

    public DispatchVehicle findByVehicleId(Long id) {
        return dispatchVehicleRepository.findByVehicleId(id);
    }
}
