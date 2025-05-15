package com.example.routing_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ORSRoute {

    private ORSSummary summary;
    private List<ORSSegment> segments;
    private List<Double> bbox;
    private String geometry;
    private List<Integer> way_points;
}
