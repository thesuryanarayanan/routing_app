package com.example.routing_backend.opt.distancematrix.provider;

import com.example.routing_backend.opt.distancematrix.DistanceMatrix;
import com.example.routing_backend.opt.distancematrix.Distance;
import com.example.routing_backend.opt.distancematrix.OptCoordinates;
import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;

@Service
public class GraphHopperDistanceProvider implements DistanceProvider {

    public static String DATA_DIR = ".data";
    public static String PBF_FILE_NAME = "turkey-latest.osm.pbf";

    private final GraphHopper graphHopper;

    public GraphHopperDistanceProvider() {
        this.graphHopper = createGraphHopperInstance();
    }

    private static GraphHopper createGraphHopperInstance() {
        var hopper = new GraphHopper();
        hopper.setOSMFile(Paths.get(DATA_DIR, PBF_FILE_NAME).toString());
        hopper.setGraphHopperLocation(".cache/routing-graph");
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        hopper.importOrLoad();
        return hopper;
    }

    @Override
    public DistanceMatrix.Entry fetch(OptCoordinates from, OptCoordinates to) {
        var ghRequest = new GHRequest(
                from.lat().doubleValue(),
                from.lon().doubleValue(),
                to.lat().doubleValue(),
                to.lon().doubleValue())
                .setProfile("car")
                .setLocale(Locale.ENGLISH);
        var ghResponse = graphHopper.route(ghRequest);

        if (ghResponse.hasErrors()) {
            throw new IllegalStateException(ghResponse.getErrors().toString());
        }

        var path = ghResponse.getBest();
        var distanceInMeters = path.getDistance();
        var timeInMs = path.getTime();
        return new DistanceMatrix.Entry(Duration.ofMillis(timeInMs), Distance.ofMeters(distanceInMeters));
    }

    public GraphHopper getGraphHopper() {
        return graphHopper;
    }
}
