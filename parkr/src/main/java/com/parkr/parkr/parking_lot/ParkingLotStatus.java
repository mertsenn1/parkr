package com.parkr.parkr.parking_lot;

public enum ParkingLotStatus
{
    OPERATIONAL("operational"),
    CLOSED_TEMPORARILY("closed_temporarily"),
    CLOSED_PERMANENTLY("closed_permanently");

    private final String status;
    ParkingLotStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
