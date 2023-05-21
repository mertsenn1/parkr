package com.parkr.parkr.common;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLotDetailModel {
    private String name;
    private Double rating;
    private Integer numOfRatings;
    private String status;
    private String placeID;
    private LocationModel coordinates;
    private Map<String, Object> fares; // for now
    private Integer capacity;
    private Integer occupancy;
    private String image;
    private Boolean hasAggreement;
}