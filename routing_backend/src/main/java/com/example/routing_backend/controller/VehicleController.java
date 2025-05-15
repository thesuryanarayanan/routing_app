package com.example.routing_backend.controller;

import com.example.routing_backend.entity.Vehicle;
import com.example.routing_backend.service.VehicleService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController

@RequestMapping("/vehicles")

@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getAllVehicles() {

        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable Long id) {

        return vehicleService.findById(id);
    }

    @PostMapping
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {

        return vehicleService.save(vehicle);
    }
}
