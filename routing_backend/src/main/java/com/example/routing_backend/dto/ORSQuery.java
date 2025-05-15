package com.example.routing_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ORSQuery {

    private List<List<Double>> coordinates;
    private String profile;
    private String format;
}
