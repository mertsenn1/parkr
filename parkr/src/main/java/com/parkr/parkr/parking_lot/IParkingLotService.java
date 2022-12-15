package com.parkr.parkr.parking_lot;
import java.util.List;

import com.google.gson.JsonElement;


public interface IParkingLotService
{
     JsonElement getNearbyLots(Double latitude, Double longitude, String language, Integer maxPrice,
                               Integer minPrice, Boolean openNow, Integer radius, String type);

     ParkingLotDto getParkingLotById(Long id);

     List<ParkingLotDto> getAllParkingLots();
                           
     ParkingLot saveParkingLot(ParkingLotDto parkingLotDto, Long ownerId);
}
