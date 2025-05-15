package com.example.routing_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "dispatch_vehicles")
public class DispatchVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany
    @JoinColumn(name = "dispatch_id")
    private List<Dispatch> dispatch;

    private Long routeDate;
}
