package com.example.routing_backend.enums;

public enum DispatchType {
    SACK(1),
    BAG(2),
    PARCEL(3),
    FILE(4);

    private final int id;

    DispatchType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DispatchType fromId(int id) {
        for (DispatchType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("ID'ye ait zimmet bulunamadı " + id);
    }
}
