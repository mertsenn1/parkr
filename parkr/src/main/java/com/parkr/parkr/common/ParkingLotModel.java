package com.parkr.parkr.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLotModel {
    private String name;
    private Double rating;
    private Double distance;
    private String placeID;
    private LocationModel coordinates;
    private Integer capacity;
    private Integer occupancy;
    private Integer lowestFare;
    private String image;
}
