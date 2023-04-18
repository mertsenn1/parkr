package com.parkr.parkr.car;

public enum FuelType
{
    DIESEL_OIL("DIESEL"),
    AUTO_GAS("AUTOGAS"),
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
