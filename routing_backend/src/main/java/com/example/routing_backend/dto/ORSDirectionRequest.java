package com.example.routing_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ORSDirectionRequest {

    private List<List<Double>> coordinates;
}
