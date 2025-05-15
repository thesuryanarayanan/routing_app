package com.example.routing_backend.opt.distancematrix;

import java.time.Duration;
import java.util.List;

public record DistanceMatrix(List<List<Entry>> entries) {

    public record Entry(Duration time, Distance distance) {

    }
}
