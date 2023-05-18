package com.parkr.parkr.car;

import com.parkr.parkr.common.ApiResponse;
import com.parkr.parkr.common.CarUpdateOperationModel;

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

    @PostMapping("/add-vehicle")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_OWNER') or hasAuthority('ADMIN')")
    public ApiResponse saveCar(@RequestBody CarDto carDto) {
        return ApiResponse.ok(carService.saveCar(carDto, carDto.getUserId()));
    }

    @PutMapping("/edit-vehicle")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_OWNER') or hasAuthority('ADMIN')")
    public ApiResponse updateCar(@RequestBody CarUpdateOperationModel carModel) {
        return ApiResponse.ok(carService.updateCar(carModel));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
    @ExceptionHandler({CarNotFoundException.class})
    public ResponseEntity<?> handleException(CarNotFoundException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
