package com.parkr.parkr.car;

import java.util.List;

import com.parkr.parkr.common.CarUpdateOperationModel;

public interface ICarService
{
    CarDto getCarById(Long id);

    List<CarDto> getAllCars();

    Car saveCar(CarDto carDto, Long userId);

    Car updateCar(CarUpdateOperationModel carModel);

    void deleteCar(Long id);

    CarDto convertToCarDto(Car car);

    Double getFuelConsumptionInLiters(Double originLatitude, Double originLongitude, Double destinationLatitude, Double destinationLongitude, FuelType emissionType);
}
