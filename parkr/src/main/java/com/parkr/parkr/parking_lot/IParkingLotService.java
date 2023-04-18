package com.parkr.parkr.parking_lot;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonElement;


public interface IParkingLotService
{
     JSONArray getNearbyLots(Double latitude, Double longitude, String language);

     JSONObject getParkingLotByPlaceID(String placeID);

     ParkingLotDto getParkingLotById(Long id);

     List<ParkingLotDto> getAllParkingLots();
                           
     ParkingLot saveParkingLot(ParkingLotDto parkingLotDto);

     void deleteParkingLot(Long id);
}
