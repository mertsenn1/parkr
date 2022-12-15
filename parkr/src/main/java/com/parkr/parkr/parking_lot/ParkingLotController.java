package com.parkr.parkr.parking_lot;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;
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
        return ApiResponse.ok(parkingLotService.saveParkingLot(parkingLotDto, parkingLotDto.getOwnerId()));
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
