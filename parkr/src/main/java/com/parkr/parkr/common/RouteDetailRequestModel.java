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
public class RouteDetailRequestModel {
    private Double originLatitude;
    private Double originLongitude;
    private String destinationPlaceID;
    private Long carID;
}