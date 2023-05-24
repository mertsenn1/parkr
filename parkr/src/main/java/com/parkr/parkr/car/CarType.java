package com.parkr.parkr.car;

public enum CarType
{
    SUV("suv"),
    HATHCHBACK("hatchback"),
    CROSSOVER("crossover"),
    CONVERTIBLE("convertible"),
    SEDAN("sedan"),
    SPORTSCAR("sportscar"),
    COUPE("coupe"),
    MINIVAN("minivan"),
    STATIONWAGON("stationwagon"),
    PICKUPTRUCK("pickuptruck"),
    UNKNOWN("UNKNOWN");

    private final String type;
    CarType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
