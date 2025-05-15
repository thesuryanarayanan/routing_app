package com.example.routing_backend.config;

import com.example.routing_backend.entity.Branch;
import com.example.routing_backend.entity.Customer;
import com.example.routing_backend.entity.Vehicle;
import com.example.routing_backend.enums.VehicleType;
import com.example.routing_backend.repository.BranchRepository;
import com.example.routing_backend.repository.CustomerRepository;
import com.example.routing_backend.repository.VehicleRepository;
import com.example.routing_backend.utility.DateUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializerConfig {

    private final BranchRepository branchRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

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
                System.out.println("Başlangıç verileri başarıyla eklendi.");
            }

        };
    }
}
