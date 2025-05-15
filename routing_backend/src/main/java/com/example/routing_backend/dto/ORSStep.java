package com.example.routing_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ORSStep {

    private double distance;
    private double duration;
    private int type;
    private String instruction;
    private String name;
    private List<Integer> way_points;
}
