package com.example.routing_backend.entity;

import com.example.routing_backend.enums.DeliveryRange;
import com.example.routing_backend.enums.DispatchType;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "dispatches")
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DispatchType dispatchType;

    private double weight;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private DeliveryRange deliveryRange;

    @Column(nullable = true)
    private Long preferFirstDeliveryTime;
    @Column(nullable = true)
    private Long preferLastDeliveryTime;

    private double receiverLatitude;
    private double receiverLongitude;

    private String receiverAddress;
}
