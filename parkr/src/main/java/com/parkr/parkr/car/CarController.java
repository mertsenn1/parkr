package com.parkr.parkr.car;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController
{
    private final ICarService carService;

    @GetMapping("{id}")
    public ApiResponse getCarById(@PathVariable Long id) {
        return ApiResponse.ok(carService.getCarById(id));
    }

    @GetMapping
    public ApiResponse getAllCars() {
        return ApiResponse.ok(carService.getAllCars());
    }

    @PostMapping()
    public ApiResponse saveCar(@RequestBody CarDto carDto) {
        return ApiResponse.ok(carService.saveCar(carDto, carDto.getUserId()));
    }
}
