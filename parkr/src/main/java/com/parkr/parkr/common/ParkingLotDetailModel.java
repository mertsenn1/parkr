package com.parkr.parkr.common;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLotDetailModel {
    private String name;
    private Double rating;
    private Double distance;
    private String placeID;
    private LocationModel coordinates;
    private JSONObject fares; // for now
    private Integer capacity;
    private Integer occupancy;
    private String image;
}