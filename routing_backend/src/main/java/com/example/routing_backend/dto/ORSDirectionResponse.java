package com.example.routing_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ORSDirectionResponse {

    private List<Double> bbox;
    private List<ORSRoute> routes;
}
