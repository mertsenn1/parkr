package com.parkr.parkr.car;

import com.parkr.parkr.common.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@SecurityRequirement(name = "parkr")
public class CarController
{
    private final ICarService carService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getCarById(@PathVariable Long id) {
        return ApiResponse.ok(carService.getCarById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LOT_OWNER') or hasAuthority('ADMIN')")
    public ApiResponse getAllCars() {
        return ApiResponse.ok(carService.getAllCars());
    }

    @PostMapping(value = "fuel")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getFuelLiter(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        Double originLatitude = jsonObject.getDouble("originLatitude");
        Double originLongitude = jsonObject.getDouble("originLongitude");
        Double destinationLatitude = jsonObject.getDouble("destinationLatitude");
        Double destinationLongitude = jsonObject.getDouble("destinationLongitude");
        String emissionType = jsonObject.getString("emissionType");
        Double fuelInLitter = carService.getFuelConsumptionInLiters(originLatitude, originLongitude, destinationLatitude, destinationLongitude, FuelType.valueOf(emissionType));
        //Double originLatitude, Double originLongitude, Double destinationLatitude, Double destinationLongitude, FuelType emissionType
        return ApiResponse.ok(fuelInLitter);
    }


    @PostMapping()
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_OWNER') or hasAuthority('ADMIN')")
    public ApiResponse saveCar(@RequestBody CarDto carDto) {
        return ApiResponse.ok(carService.saveCar(carDto, carDto.getUserId()));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
