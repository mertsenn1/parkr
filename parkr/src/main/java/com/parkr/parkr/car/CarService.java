package com.parkr.parkr.car;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.parkr.parkr.common.CarUpdateOperationModel;
import com.parkr.parkr.common.GoogleServices;
import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService implements ICarService
{
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public CarDto getCarById(Long id)
    {
        Optional<Car> car = carRepository.findById(id);

        if (car.isEmpty()) return null;

        log.info("Car with the id: {} is requested", id);

        return convertToCarDto(car.get());
    }

    @Override
    public List<CarDto> getAllCars()
    {
        List<CarDto> cars = carRepository.findAll().stream().map(this::convertToCarDto).toList();

        log.info("All cars are requested with the size: {}", cars.size());

        return cars;
    }

    @Override
    public Car saveCar(CarDto carDto, Long userId)
    {
        Car car;
        User user;
        try
        {
            user = userRepository.findById(userId).get();

            car = carRepository.save(convertToCar(carDto, user));
            log.info("Car is saved with id: {}", car.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the car, error: {}", ex.getMessage());
            return null;
        }
        return car;
    }

    @Override
    public Car updateCar(CarUpdateOperationModel carModel)
    {
        Optional<Car> car = carRepository.findById(carModel.getId());

        if (car.isEmpty()) return null;

        

        Car newCar = convertToCar(carModel, car.get().getUser());
        newCar.setId(carModel.getId());
        Car updatedCar = carRepository.save(newCar);

        log.info("Car is updated with id: {}", updatedCar.getId());

        return updatedCar;
    }

    @Override
    public void deleteCar(Long id){
        Optional<Car> lotSummary = carRepository.findById(id);
        if (!lotSummary.isPresent()) throw new CarNotFoundException("Car couldn't found by id: " + id);
        try{
            carRepository.delete(lotSummary.get());
            log.info("Car with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the car, error: {}", ex.getMessage());
            throw new RuntimeException("Error occurred while deleting the car, error: {}");
        }
    }

    @Override
    public Double getFuelConsumptionInLiters(Double originLatitude, Double originLongitude, Double destinationLatitude, Double destinationLongitude, FuelType emissionType){
        JSONObject jsonResponse = GoogleServices.getEcoFriendlyRoute(originLatitude, originLongitude, destinationLatitude, destinationLongitude, emissionType.toString());
        JSONArray routes = jsonResponse.getJSONArray("routes");
        double fuelConsumptionMicroliters = -1;
    
        for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            JSONArray routeLabels = route.getJSONArray("routeLabels");
    
            for (int j = 0; j < routeLabels.length(); j++) {
                String label = routeLabels.getString(j);
    
                if ("FUEL_EFFICIENT".equals(label)) {
                    JSONObject travelAdvisory = route.getJSONObject("travelAdvisory");
                    fuelConsumptionMicroliters = travelAdvisory.getDouble("fuelConsumptionMicroliters");
                    break;
                }
            }
    
            if (fuelConsumptionMicroliters != -1) {
                break;
            }
        }
    
        if (fuelConsumptionMicroliters == -1) {
            throw new RuntimeException("FUEL_EFFICIENT route not found in the response");
        }
    
        // Convert microliters to liters
        double fuelConsumptionLiters = fuelConsumptionMicroliters / 1_000_000;
        return fuelConsumptionLiters;
    }

    public CarDto convertToCarDto(Car car) {
        return CarDto.builder()
                .id(car.getId())
                .plate(car.getPlate())
                .carType(car.getCarType())
                .model(car.getModel())
                .fuelType(car.getFuelType())
                .userId(((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId())
                .build();
    }

    private Car convertToCar(CarDto carDto, User user) {
        return new Car(null, carDto.getPlate(), carDto.getCarType(), carDto.getModel(), carDto.getFuelType(), user);
    }

    private Car convertToCar(CarUpdateOperationModel carModel, User user) {
        return new Car(null, carModel.getPlate(), carModel.getCarType(), carModel.getModel(), carModel.getFuelType(), user);
    }
}
