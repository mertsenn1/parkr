package com.parkr.parkr.common;

import com.parkr.parkr.car.CarType;
import com.parkr.parkr.car.FuelType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarUpdateOperationModel {
    
    private Long id;

    private String plate;

    private CarType carType;

    private String model;

    private FuelType fuelType;
}
