package com.parkr.parkr.car;

import com.parkr.parkr.common.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

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
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse getCarById(@PathVariable Long id) {
        return ApiResponse.ok(carService.getCarById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LOT_OWNER')")
    public ApiResponse getAllCars() {
        return ApiResponse.ok(carService.getAllCars());
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse saveCar(@RequestBody CarDto carDto) {
        return ApiResponse.ok(carService.saveCar(carDto, carDto.getUserId()));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        carService.deleteCar(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
