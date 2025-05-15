package com.example.routing_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ORSSegment {

    private double distance;
    private double duration;
    private List<ORSStep> steps;
}
