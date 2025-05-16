package com.example.routing_backend.config;

import com.example.routing_backend.entity.Branch;
import com.example.routing_backend.entity.Customer;
import com.example.routing_backend.entity.Dispatch;
import com.example.routing_backend.entity.Vehicle;
import com.example.routing_backend.enums.DeliveryRange;
import com.example.routing_backend.enums.DispatchType;
import com.example.routing_backend.enums.VehicleType;
import com.example.routing_backend.repository.BranchRepository;
import com.example.routing_backend.repository.CustomerRepository;
import com.example.routing_backend.repository.DispatchRepository;
import com.example.routing_backend.repository.VehicleRepository;
import com.example.routing_backend.utility.DateUtility;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.agreement.srp.SRP6Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataInitializerConfig {

    private final BranchRepository branchRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final DispatchRepository dispatchRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    public ApplicationRunner dataInitializer() {
        return args -> {
            if (ddlAuto.equals("create")) {
                Branch branch = new Branch();
                branch.setName("Sanko Şube");
                branch.setLatitude(41.1001871);
                branch.setLongitude(28.8892948);
                branch.setBoundingBoxLatitude1(41.1189);
                branch.setBoundingBoxLongitude1(28.8096);
                branch.setBoundingBoxLatitude2(41.0710);
                branch.setBoundingBoxLongitude2(28.8922);
                branchRepository.save(branch);

                Vehicle vehicle = new Vehicle();
                vehicle.setBranch(branch);
                vehicle.setVehicleType(VehicleType.TRUCK);
                vehicle.setLicensePlate("34ABC123");
                vehicle.setDeliveryRange(DateUtility.getDeliveryRange(1735645406L));
                vehicleRepository.save(vehicle);

                Vehicle vehicle1 = new Vehicle();
                vehicle1.setBranch(branch);
                vehicle1.setVehicleType(VehicleType.TRUCK);
                vehicle1.setLicensePlate("34ABC223");
                vehicle1.setDeliveryRange(DateUtility.getDeliveryRange(1735617926L));
                vehicleRepository.save(vehicle1);
                for (int i = 0; i < 5; i++) {
                    Customer customer = new Customer();
                    customer.setFirstName("Name" + i);
                    customer.setLastName("Surname" + i);
                    customer.setPhone("5555555555");
                    customerRepository.save(customer);
                }

                List<HashMap<String,Double>> coordinates = List.of(
                        new HashMap<>(Map.of("latitude", 41.09768333375933, "longitude", 28.88992153956545)),
                        new HashMap<>(Map.of("latitude", 41.1002360511431, "longitude", 28.89185302123544)),
                        new HashMap<>(Map.of("latitude", 41.09779438743708, "longitude", 28.885973619325004)),
                        new HashMap<>(Map.of("latitude", 41.10138408681063, "longitude", 28.880931066591593)),
                        new HashMap<>(Map.of("latitude", 41.10196673483382, "longitude", 28.877557886397234))
                );

                for (int i = 0; i < 5; i++) {
                    Dispatch dispatch = new Dispatch();
                    dispatch.setCustomer(customerRepository.findById((long) (i + 1)).orElse(null));
                    dispatch.setDispatchType(DispatchType.FILE);
                    dispatch.setReceiverLatitude(coordinates.get(i).get("latitude"));
                    dispatch.setReceiverLongitude(coordinates.get(i).get("longitude"));
                    dispatch.setReceiverAddress("Sanko Park AVM");
                    dispatch.setDeliveryRange(DeliveryRange.EVENING);
                    dispatch.setWeight(0.5);
                    dispatchRepository.save(dispatch);
                }

                System.out.println("Başlangıç verileri başarıyla eklendi.");
            }

        };
    }
}
