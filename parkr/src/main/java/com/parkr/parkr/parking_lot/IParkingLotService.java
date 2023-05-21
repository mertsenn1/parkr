package com.parkr.parkr.parking_lot;
import java.util.List;

import com.parkr.parkr.car.FuelType;
import com.parkr.parkr.common.ParkingLotDetailModel;
import com.parkr.parkr.common.ParkingLotModel;
import com.parkr.parkr.common.RouteDetailsModel;


public interface IParkingLotService
{
     List<ParkingLotModel> getNearbyLots(Double latitude, Double longitude);

     ParkingLotDetailModel getParkingLotByPlaceID(String placeID);

     ParkingLotDto getParkingLotById(Long id);

     List<ParkingLotDto> getAllParkingLots();
                           
     ParkingLot saveParkingLot(ParkingLotDto parkingLotDto);

     void deleteParkingLot(Long id);

     void enterParkingLot(String plate, Long parkingLotID);
     void exitParkingLot(String plate, Long parkingLotID);

     List<RouteDetailsModel> getRouteDetails(Double originLatitude, Double originLongitude, String destinationPlaceID, Long carID);

     ParkingLot updateParkingLotFares(String fares);
}
