package com.parkr.parkr.parking_lot;

import com.parkr.parkr.common.ApiResponse;
import com.parkr.parkr.common.LocationModel;
import com.parkr.parkr.common.ParkingLotOperationModel;
import com.parkr.parkr.common.PlaceDetailRequestModel;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@SecurityRequirement(name = "parkr")
public class ParkingLotController
{
    private final IParkingLotService parkingLotService;

    @PostMapping(value = "/nearby")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getNearbyParkingLots(@RequestBody LocationModel location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        return ApiResponse.ok(parkingLotService.getNearbyLots(latitude, longitude));
    }

    @PostMapping(value = "/place-details")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getPlaceDetails(@RequestBody PlaceDetailRequestModel place) {
        return ApiResponse.ok(parkingLotService.getParkingLotByPlaceID(place.getPlaceID()));
    }

    @PostMapping(value = "/entry")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse enterParkingLot(@RequestBody ParkingLotOperationModel entryInfo) {
        parkingLotService.enterParkingLot(entryInfo.getLicensePlate(), entryInfo.getParkingLotID());
        return ApiResponse.ok();
    }

    @PostMapping(value = "/exit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse exitParkingLot(@RequestBody ParkingLotOperationModel exitInfo) {
        parkingLotService.exitParkingLot(exitInfo.getLicensePlate(), exitInfo.getParkingLotID());
        return ApiResponse.ok();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse getParkingLotById(@PathVariable Long id) {
        return ApiResponse.ok(parkingLotService.getParkingLotById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse getAllParkingLots() {
        return ApiResponse.ok(parkingLotService.getAllParkingLots());
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse saveParkingLot(@RequestBody ParkingLotDto parkingLotDto) {
        return null;
        //return ApiResponse.ok(parkingLotService.saveParkingLot(parkingLotDto, parkingLotDto.getOwnerId()));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
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
