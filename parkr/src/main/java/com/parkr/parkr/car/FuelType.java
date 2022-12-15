package com.parkr.parkr.car;

public enum FuelType
{
    DIESEL_OIL("diesel_oil"),
    FUEL_OIL("fuel_oil"),
    GASOLINE("gasoline"),
    ELECTRIC("electric");

    private final String type;
    FuelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
