package com.parkr.parkr.car;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public CarDto convertToCarDto(Car car) {
        return CarDto.builder()
                .id(car.getId())
                .plate(car.getPlate())
                .carType(car.getCarType())
                .model(car.getModel())
                .fuelType(car.getFuelType())
                .build();
    }

    private Car convertToCar(CarDto carDto, User user) {
        return new Car(null, carDto.getPlate(), carDto.getCarType(), carDto.getModel(), carDto.getFuelType(), user);
    }
}
