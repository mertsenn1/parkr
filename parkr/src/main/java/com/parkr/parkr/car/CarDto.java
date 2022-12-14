package com.parkr.parkr.car;


import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CarDto
{
    private String plate;

    private CarType carType;

    private String model;

    private FuelType fuelType;
}
