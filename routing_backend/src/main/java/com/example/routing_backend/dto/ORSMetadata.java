package com.example.routing_backend.dto;

import lombok.Data;

@Data
public class ORSMetadata {

    private String attribution;
    private String service;
    private long timestamp;
    private ORSQuery query;
    private ORSEngine engine;
}
