package com.parkr.parkr.car;

import java.util.List;

public interface ICarService
{
    CarDto getCarById(Long id);

    List<CarDto> getAllCars();

    Car saveCar(CarDto carDto, Long userId);

    CarDto convertToCarDto(Car car);
}
