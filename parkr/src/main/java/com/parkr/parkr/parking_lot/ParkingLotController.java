package com.parkr.parkr.parking_lot;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("parkingLots")
public class ParkingLotController
{
    private final IParkingLotService parkingLotService;

    @PostMapping(value = "/nearby")
    public ApiResponse getNearbyParkingLots(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        Double latitude = jsonObject.getDouble("latitude");
        Double longitude = jsonObject.getDouble("longitude");
        JSONArray results = parkingLotService.getNearbyLots(latitude, longitude, "en");
        return ApiResponse.ok(results.toList());
    }

    @PostMapping(value = "/place-details")
    public ApiResponse getPlaceDetails(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        String placeID = jsonObject.getString("placeID");
        JSONObject result = parkingLotService.getParkingLotByPlaceID(placeID);
        if (result == null) {
            return ApiResponse.ok(new JSONObject()); // will change.
        }
        return ApiResponse.ok(result.toMap());
    }

    @GetMapping("{id}")
    public ApiResponse getParkingLotById(@PathVariable Long id) {
        return ApiResponse.ok(parkingLotService.getParkingLotById(id));
    }

    @GetMapping
    public ApiResponse getAllParkingLots() {
        return ApiResponse.ok(parkingLotService.getAllParkingLots());
    }

    @PostMapping()
    public ApiResponse saveParkingLot(@RequestBody ParkingLotDto parkingLotDto) {
        return null;
        //return ApiResponse.ok(parkingLotService.saveParkingLot(parkingLotDto, parkingLotDto.getOwnerId()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        parkingLotService.deleteParkingLot(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

        /* 
    @GetMapping
    ApiResponse getLocation(@RequestParam Double latitude, @RequestParam Double longitude,
                            @RequestParam(required=false) String language,
                            @RequestParam(required=false) Integer maxPrice,
                            @RequestParam(required=false) Integer minPrice,
                            @RequestParam(required=false) Boolean openNow,
                            @RequestParam(required=false) Integer radius,
                            @RequestParam(required=false) String type) {
        return ApiResponse.ok(parkingLotService.getNearbyLots(latitude, longitude, language, maxPrice, minPrice,
                openNow, radius, type));
    }
    */
}
