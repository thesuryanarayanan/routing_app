package com.example.routing_backend.controller;

import com.example.routing_backend.entity.Branch;
import com.example.routing_backend.entity.Dispatch;
import com.example.routing_backend.entity.DispatchVehicle;
import com.example.routing_backend.entity.Vehicle;
import com.example.routing_backend.service.DispatchService;
import com.example.routing_backend.service.DispatchVehicleService;
import com.example.routing_backend.service.VehicleService;
import com.example.routing_backend.utility.BoundingBoxUtility;
import com.example.routing_backend.utility.DateUtility;

import com.example.routing_backend.utility.DeliveryRangeUtility;
import com.graphhopper.GHRequest;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.algorithm.PrettyAlgorithmBuilder;
import com.graphhopper.jsprit.core.algorithm.SearchStrategy;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.acceptor.GreedyAcceptance;
import com.graphhopper.jsprit.core.algorithm.listener.IterationStartsListener;
import com.graphhopper.jsprit.core.algorithm.module.RuinAndRecreateModule;
import com.graphhopper.jsprit.core.algorithm.recreate.InsertionBuilder;
import com.graphhopper.jsprit.core.algorithm.recreate.RegretInsertion;
import com.graphhopper.jsprit.core.algorithm.ruin.RadialRuinStrategyFactory;
import com.graphhopper.jsprit.core.algorithm.ruin.RandomRuinStrategyFactory;
import com.graphhopper.jsprit.core.algorithm.ruin.RuinStrategy;
import com.graphhopper.jsprit.core.algorithm.ruin.distance.AvgServiceAndShipmentDistance;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.SolutionCostCalculator;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.FiniteFleetManagerFactory;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleFleetManager;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.example.routing_backend.opt.distancematrix.DistanceMatrix;
import com.example.routing_backend.opt.distancematrix.OptCoordinates;
import com.example.routing_backend.opt.distancematrix.provider.GraphHopperDistanceProvider;

import java.math.BigDecimal;

@RestController
@RequestMapping("/dispatch-vehicles")
@RequiredArgsConstructor
public class DispatchVehicleController {

    private final DispatchVehicleService dispatchVehicleService;
    private final DispatchService dispatchService;
    private final VehicleService vehicleService;
    private final Random random = new Random();
    private final GraphHopperDistanceProvider graphHopperDistanceProvider;

    Logger logger = LoggerFactory.getLogger(DispatchVehicleController.class);

    @GetMapping
    public List<DispatchVehicle> getAllDispatchVehicles() {
        List<DispatchVehicle> dispatchVehicles = dispatchVehicleService.findAll();
        dispatchVehicles.removeIf(d -> d.getDispatch().isEmpty());
        return dispatchVehicles;
    }

    @GetMapping("/{id}")

    public DispatchVehicle getDispatchVehicle(@PathVariable Long id) {
        return dispatchVehicleService.findById(id);
    }

    @PostMapping
    public ResponseEntity createDispatchVehicle(@RequestBody DispatchVehicle dispatchVehicle) {
        List<Dispatch> dispatch = new ArrayList<Dispatch>();
        Vehicle vehicle = vehicleService.findById(dispatchVehicle.getVehicle().getId());
        DispatchVehicle existingDispatchVehicle = dispatchVehicleService.findByVehicleId(vehicle.getId());
        if (existingDispatchVehicle != null) {
            long id = existingDispatchVehicle.getId();
            dispatchVehicleService.deleteById(id);
        }
        dispatchVehicle.getDispatch().forEach(d -> {
            Dispatch newD = dispatchService.findById(d.getId());
            boolean isWithinBoundingBox = BoundingBoxUtility.isWithinBoundingBox(
                    newD.getReceiverLatitude(),
                    newD.getReceiverLongitude(),
                    vehicle.getBranch().getBoundingBoxLatitude1(),
                    vehicle.getBranch().getBoundingBoxLongitude1(),
                    vehicle.getBranch().getBoundingBoxLatitude2(),
                    vehicle.getBranch().getBoundingBoxLongitude2()
            );
            if (DeliveryRangeUtility.isInRange(newD.getDeliveryRange(), vehicle.getDeliveryRange()) && isWithinBoundingBox) {
                dispatch.add(newD);
            }
        });
        if (dispatch.isEmpty()) {
            return ResponseEntity.badRequest().body("Zimmet bos olamaz");
        }
        dispatchVehicle.setDispatch(dispatch);
        dispatchVehicle.setRouteDate(Instant.now().getEpochSecond());
        return ResponseEntity.ok(dispatchVehicleService.save(dispatchVehicle));
    }

    @GetMapping("/route/{id}")

    public ResponseEntity createRouting(@PathVariable Long id) {
        DispatchVehicle dispatchVehicle = dispatchVehicleService.findById(id);
        Vehicle vehicle = dispatchVehicle.getVehicle();
        Branch branch = vehicle.getBranch();
        List<Dispatch> dispatchList = dispatchVehicle.getDispatch();

        List<Location> locations = new ArrayList<>();

        locations.add(Location.newInstance(branch.getLatitude(), branch.getLongitude()));

        dispatchList.forEach(d
                -> locations.add(Location.newInstance(d.getReceiverLatitude(), d.getReceiverLongitude()))
        );
        logger.info("Locations: " + locations);

        double[][] distanceMatrix = new double[locations.size()][locations.size()];
        double[][] timeMatrix = new double[locations.size()][locations.size()];

        List<OptCoordinates> optCoordinates = locations.stream()
                .map(location -> new OptCoordinates(
                BigDecimal.valueOf(location.getCoordinate().getX()),
                BigDecimal.valueOf(location.getCoordinate().getY())
        ))
                .toList();

        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                if (i != j) {
                    OptCoordinates from = optCoordinates.get(i);
                    OptCoordinates to = optCoordinates.get(j);

                    DistanceMatrix.Entry entry = graphHopperDistanceProvider.fetch(from, to);

                    distanceMatrix[i][j] = entry.distance().meters().doubleValue();
                    timeMatrix[i][j] = entry.time().getSeconds();
                }
            }
        }

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        for (int i = 0; i < dispatchList.size(); i++) {
            Dispatch d = dispatchList.get(i);

            int serviceTime = (random.nextInt(10) + 1) * 60;

            if (d.getPreferFirstDeliveryTime() != null) {
                vrpBuilder.addJob(Service.Builder.newInstance(d.getId().toString())
                        .addSizeDimension(0, 1)
                        .setLocation(locations.get(i + 1))
                        .setServiceTime(serviceTime)
                        .addTimeWindow(d.getPreferFirstDeliveryTime(), d.getPreferLastDeliveryTime())
                        .build());
            } else {
                vrpBuilder.addJob(Service.Builder.newInstance(d.getId().toString())
                        .addSizeDimension(0, 1)
                        .setLocation(locations.get(i + 1))
                        .setServiceTime(serviceTime)
                        .build());
            }
        }

        VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(vehicle.getBranch().getId().toString())
                .addCapacityDimension(0, 9999)
                .setCostPerDistance(1.0)
                .setFixedCost(100)
                .build();

        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicle.getId().toString());
        vehicleBuilder.setStartLocation(locations.get(0))
                .setType(vehicleType);

        List<Long> deliveryHours = DateUtility.getDeliveryRangeHours(vehicle.getDeliveryRange());
        Long startTime = deliveryHours.get(0);
        Long endTime = deliveryHours.get(1);

        vehicleBuilder
                .setEarliestStart(startTime)
                .setLatestArrival(endTime);

        vrpBuilder.addVehicle(vehicleBuilder.build());

        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                matrixBuilder.addTransportTime(locations.get(i).getId(), locations.get(j).getId(), timeMatrix[i][j]);
                matrixBuilder.addTransportDistance(locations.get(i).getId(), locations.get(j).getId(), distanceMatrix[i][j]);
            }
        }
        vrpBuilder.setRoutingCost(matrixBuilder.build());

        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
        VehicleRoutingProblem vrp = vrpBuilder.build();
        VehicleRoutingAlgorithm vra = createAlgorithm(vrp);
        vra.setMaxIterations(10);
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
        VehicleRoutingProblemSolution solution = Solutions.bestOf(solutions);

        Map<String, Object> response = new HashMap<>();
        response.put("unassignedJobs", solution.getUnassignedJobs().stream()
                .map(job -> Map.of(
                "jobId", job.getId(),
                "location", Map.of(
                        "latitude", ((Service) job).getLocation().getCoordinate().getX(),
                        "longitude", ((Service) job).getLocation().getCoordinate().getY()
                )
        ))
                .collect(Collectors.toList()));

        List<Map<String, Object>> routes = new ArrayList<>();
        List<List<Double>> coordinates = new ArrayList<>();
        solution.getRoutes().forEach(route -> {
            Map<String, Object> routeInfo = new HashMap<>();
            routeInfo.put("vehicleId", route.getVehicle().getId());
            routeInfo.put("startTime", formatTimestamp(route.getStart().getEndTime()));

            List<Map<String, Object>> services = new ArrayList<>();
            route.getActivities().forEach(activity -> {
                Map<String, Object> service = new HashMap<>();
                if (activity.getLocation() != null) {
                    service.put("location", Map.of(
                            "latitude", activity.getLocation().getCoordinate().getX(),
                            "longitude", activity.getLocation().getCoordinate().getY()
                    ));
                    coordinates.add(Arrays.asList(activity.getLocation().getCoordinate().getY(), activity.getLocation().getCoordinate().getX()));
                }
                service.put("arrivalTime", formatTimestamp(activity.getArrTime()));
                service.put("endTime", formatTimestamp(activity.getEndTime()));
                service.put("duration", activity.getOperationTime());
                services.add(service);
            });
            routeInfo.put("services", services);
            routes.add(routeInfo);
        });
        response.put("routes", routes);
        coordinates.add(0, List.of(branch.getLongitude(), branch.getLatitude()));
        coordinates.add(List.of(branch.getLongitude(), branch.getLatitude()));
        if (coordinates.size() < 3) {
            return ResponseEntity.badRequest().body("Dagitima ait zimmet bulunamadi!");
        } else {
            Map<String, List<List<Double>>> routeGeometry = new HashMap<>();
            List<List<Double>> outboundRoute = new ArrayList<>();
            List<List<Double>> returnRoute = new ArrayList<>();

            for (int i = 0; i < coordinates.size() - 1; i++) {
                List<Double> from = coordinates.get(i);
                List<Double> to = coordinates.get(i + 1);

                var ghRequest = new GHRequest(
                        from.get(1),
                        from.get(0),
                        to.get(1),
                        to.get(0)
                )
                        .setProfile("car")
                        .setLocale(Locale.ENGLISH);

                var ghResponse = graphHopperDistanceProvider.getGraphHopper().route(ghRequest);

                if (ghResponse.hasErrors()) {
                    throw new IllegalStateException(ghResponse.getErrors().toString());
                }

                var pointList = ghResponse.getBest().getPoints();
                List<List<Double>> segmentPoints = new ArrayList<>();
                for (int j = 0; j < pointList.size(); j++) {
                    segmentPoints.add(Arrays.asList(
                            pointList.getLon(j),
                            pointList.getLat(j)
                    ));
                }

                if (i < coordinates.size() - 2) {
                    outboundRoute.addAll(segmentPoints);
                } else {

                    returnRoute.addAll(segmentPoints);
                }
            }
            routeGeometry.put("outbound", outboundRoute);
            routeGeometry.put("return", returnRoute);
            response.put("geometry", routeGeometry);
        }
        response.put("vehicle", vehicle);
        response.put("dispatches", dispatchList);
        response.put("totalDistance", Math.round(solution.getCost()));

        solution.getRoutes().forEach(route -> {
            double totalDuration = 0;
            TourActivity prevAct = route.getStart();

            for (TourActivity act : route.getActivities()) {

                double travelTime = vrp.getTransportCosts().getTransportTime(
                        prevAct.getLocation(),
                        act.getLocation(),
                        prevAct.getEndTime(),
                        route.getDriver(),
                        route.getVehicle()
                );

                totalDuration += travelTime;

                totalDuration += act.getOperationTime();

                prevAct = act;
            }

            totalDuration += vrp.getTransportCosts().getTransportTime(
                    prevAct.getLocation(),
                    route.getEnd().getLocation(),
                    prevAct.getEndTime(),
                    route.getDriver(),
                    route.getVehicle()
            );

            response.put("totalDurationMinutes", Math.round(totalDuration / 60.0));

            long hours = (long) (totalDuration / 3600);
            long minutes = (long) ((totalDuration % 3600) / 60);
            response.put("totalDurationFormatted", String.format("%d saat %d dakika", hours, minutes));
        });

        return ResponseEntity.ok(response);
    }

    public static VehicleRoutingAlgorithm createAlgorithm(final VehicleRoutingProblem vrp) {
        VehicleFleetManager fleetManager = new FiniteFleetManagerFactory(vrp.getVehicles()).createFleetManager();
        StateManager stateManager = new StateManager(vrp);
        ConstraintManager constraintManager = new ConstraintManager(vrp, stateManager);
        InsertionBuilder iBuilder = new InsertionBuilder(vrp, fleetManager, stateManager, constraintManager);
        iBuilder.setInsertionStrategy(InsertionBuilder.Strategy.REGRET);
        RegretInsertion regret = (RegretInsertion) iBuilder.build();
        RuinStrategy randomRuin = new RandomRuinStrategyFactory(0.5).createStrategy(vrp);
        RuinStrategy radialRuin = new RadialRuinStrategyFactory(0.3, new AvgServiceAndShipmentDistance(vrp.getTransportCosts())).createStrategy(vrp);
        SolutionCostCalculator objectiveFunction = getObjectiveFunction(vrp);
        SearchStrategy firstStrategy = new SearchStrategy("firstStrategy", new SelectBest(), new GreedyAcceptance(1), objectiveFunction);
        firstStrategy.addModule(new RuinAndRecreateModule("randRuinRegretIns", regret, randomRuin));
        SearchStrategy secondStrategy = new SearchStrategy("secondStrategy", new SelectBest(), new GreedyAcceptance(1), objectiveFunction);
        secondStrategy.addModule(new RuinAndRecreateModule("radRuinRegretIns", regret, radialRuin));
        SearchStrategy thirdStrategy = new SearchStrategy("thirdStrategy", new SelectBest(), new GreedyAcceptance(1), objectiveFunction);
        secondStrategy.addModule(new RuinAndRecreateModule("radRuinBestIns", regret, radialRuin));
        PrettyAlgorithmBuilder prettyAlgorithmBuilder = PrettyAlgorithmBuilder.newInstance(vrp, fleetManager, stateManager, constraintManager);
        final VehicleRoutingAlgorithm vra = prettyAlgorithmBuilder
                .withStrategy(firstStrategy, 0.1).withStrategy(secondStrategy, 0.5).withStrategy(thirdStrategy, 0.5)
                .addCoreStateAndConstraintStuff()
                .constructInitialSolutionWith(regret, objectiveFunction)
                .build();

        IterationStartsListener strategyAdaptor = new IterationStartsListener() {
            @Override
            public void informIterationStarts(int i, VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
                if (i == 50) {
                    vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy", 0.0);
                }
                if (i == 90) {
                    vra.getSearchStrategyManager().informStrategyWeightChanged("firstStrategy", 0.7);
                }
            }
        };
        vra.addListener(strategyAdaptor);
        return vra;
    }

    private static SolutionCostCalculator getObjectiveFunction(final VehicleRoutingProblem vrp) {
        return solution -> {
            SolutionAnalyser analyser = new SolutionAnalyser(vrp, solution, (from, to, departureTime, vehicle) -> vrp.getTransportCosts().getTransportCost(from, to, 0., null, null));
            return analyser.getVariableTransportCosts() + solution.getUnassignedJobs().size() * 500.;
        };
    }

    private String formatTimestamp(double timestamp) {
        return Instant.ofEpochSecond((long) timestamp)
                .atZone(ZoneId.of("Europe/Istanbul"))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
