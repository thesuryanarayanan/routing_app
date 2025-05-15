package com.example.routing_backend.enums;

public enum VehicleType {
    PANELVAN(1),
    LORRY(2),
    TRUCK(3);

    private final int id;

    VehicleType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static VehicleType fromId(int id) {
        for (VehicleType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("No VehicleType with id " + id);
    }
}
