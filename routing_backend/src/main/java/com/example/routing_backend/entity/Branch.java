package com.example.routing_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double latitude;
    private double longitude;

    private double boundingBoxLatitude1;
    private double boundingBoxLongitude1;
    private double boundingBoxLatitude2;
    private double boundingBoxLongitude2;
}
