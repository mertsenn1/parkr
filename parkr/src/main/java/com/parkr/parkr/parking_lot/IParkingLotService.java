package com.parkr.parkr.parking_lot;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.parkr.parkr.common.ParkingLotDetailModel;
import com.parkr.parkr.common.ParkingLotModel;


public interface IParkingLotService
{
     List<ParkingLotModel> getNearbyLots(Double latitude, Double longitude);

     ParkingLotDetailModel getParkingLotByPlaceID(String placeID);

     ParkingLotDto getParkingLotById(Long id);

     List<ParkingLotDto> getAllParkingLots();
                           
     ParkingLot saveParkingLot(ParkingLotDto parkingLotDto);

     void deleteParkingLot(Long id);
}
