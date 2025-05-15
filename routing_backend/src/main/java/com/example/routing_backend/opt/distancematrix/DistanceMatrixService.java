package com.example.routing_backend.opt.distancematrix;

import com.example.routing_backend.opt.distancematrix.provider.DistanceProvider;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DistanceMatrixService {

    private final DistanceProvider distanceProvider;

    public DistanceMatrixService(DistanceProvider distanceProvider) {
        this.distanceProvider = distanceProvider;
    }

    public DistanceMatrix createDistanceMatrix(List<OptCoordinates> coordinates) {

        var entries = coordinates.stream()
                .flatMap(from -> coordinates.stream()
                .map(to -> Tuple.of(from, to, getEntry(from, to))))
                .toList();

        return createDistanceMatrix(coordinates, entries);
    }

    private DistanceMatrix.Entry getEntry(OptCoordinates from, OptCoordinates to) {

        if (from.equals(to)) {
            return new DistanceMatrix.Entry(Duration.ZERO, Distance.ZERO);
        }

        return distanceProvider.fetch(from, to);
    }

    private DistanceMatrix createDistanceMatrix(List<OptCoordinates> coordinates, List<Tuple3<OptCoordinates, OptCoordinates, DistanceMatrix.Entry>> entries) {

        var map = entries.stream()
                .collect(Collectors.toMap(
                        t -> Tuple.of(t._1(), t._2()),
                        Tuple3::_3
                ));

        var entriesOrdered = coordinates.stream()
                .map(from -> coordinates.stream()
                .map(to -> map.get(Tuple.of(from, to)))
                .toList())
                .toList();

        return new DistanceMatrix(entriesOrdered);
    }
}
