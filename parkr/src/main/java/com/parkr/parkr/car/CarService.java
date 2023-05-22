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
    public Car saveCar(CarDto carDto)
    {
        Car car;
        User user;
        try
        {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Car> car = carRepository.findById(carModel.getId());

        if (!car.isPresent()) return null;

        if (!(user.getId()).equals(car.get().getUser().getId())) {
            log.info("Car does not belong to the user, car id: {}, user id: {}", carModel.getId(), user.getId());
            return null;
        }

        Car newCar = convertToCar(carModel, user);
        newCar.setId(carModel.getId());
        Car updatedCar = carRepository.save(newCar);

        log.info("Car is updated with id: {}", updatedCar.getId());

        return updatedCar;
    }

    @Override
    public void deleteCar(Long id){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Car> car = carRepository.findById(id);
        if (!car.isPresent()) throw new CarNotFoundException("Car couldn't found by id: " + id);
        if (!(user.getId().equals(car.get().getUser().getId()))) throw new CarNotFoundException("User does not own the specified car: " + id);
        
        try{
            carRepository.delete(car.get());
            log.info("Car with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the car, error: {}", ex.getMessage());
            throw new RuntimeException("Error occurred while deleting the car, error: {}");
        }
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
