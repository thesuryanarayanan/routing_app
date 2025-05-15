package com.example.routing_backend.entity;

import com.example.routing_backend.enums.DeliveryRange;
import com.example.routing_backend.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    //private Long departureTime; 
    @Enumerated(EnumType.STRING)
    private DeliveryRange deliveryRange;
}
