package com.parkr.parkr.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentParkingLotModel {
    private String name;
    private String image;
    private Double rating;
    private Integer numOfRatings;
    private String placeID;
    private LocationModel coordinates;
}