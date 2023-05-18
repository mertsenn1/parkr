package com.parkr.parkr.car;

public enum FuelType
{
    DIESEL("DIESEL"),
    GASOLINE("GASOLINE"),
    HYBRID("HYBRID"),
    ELECTRIC("ELECTRIC");

    private final String type;
    FuelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
