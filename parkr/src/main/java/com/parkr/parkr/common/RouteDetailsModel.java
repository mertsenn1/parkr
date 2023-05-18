package com.parkr.parkr.common;

import com.parkr.parkr.car.FuelType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteDetailsModel {
    private String duration;
    private Double distance;
    private String polyline;
    private String routeToken;
    private FuelType fuelType;
    private Double fuelConsumptionInLiters;
    private Integer capacity;
    private Integer occupancy;
}
