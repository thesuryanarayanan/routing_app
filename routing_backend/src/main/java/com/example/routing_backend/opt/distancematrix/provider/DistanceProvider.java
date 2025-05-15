package com.example.routing_backend.opt.distancematrix.provider;

import com.example.routing_backend.opt.distancematrix.DistanceMatrix;
import com.example.routing_backend.opt.distancematrix.OptCoordinates;

public interface DistanceProvider {

    DistanceMatrix.Entry fetch(OptCoordinates from, OptCoordinates to);
}
