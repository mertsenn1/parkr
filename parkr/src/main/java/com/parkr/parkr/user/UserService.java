package com.parkr.parkr.user;

import com.parkr.parkr.address.Address;
import com.parkr.parkr.address.AddressDto;
import com.parkr.parkr.address.IAddressService;
import com.parkr.parkr.car.Car;
import com.parkr.parkr.car.CarDto;
import com.parkr.parkr.car.ICarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService
{
    private final UserRepository userRepository;
    private final IAddressService addressService;
    private final ICarService carService;

    @Override
    public UserDto getUserById(Long id)
    {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) return null;

        log.info("User with the id: {} is requested", id);

        return convertToUserDto(user.get());
    }

    
    @Override
    public List<UserDto> getAllCustomers()
    {
        return null;
        /* 
        List<UserDto> customers = userRepository.findAllByIsOwnerIsFalse().stream().map(this::convertToUserDto).toList();
        log.info("All customers are requested with the size: {}", customers.size());
        return customers;
        */
    }
    

    /* */
    @Override
    public List<UserDto> getAllOwners()
    {
        return null;
        /* 
        List<UserDto> owners = userRepository.findAllByIsOwnerIsTrue().stream().map(this::convertToUserDto).toList();
        log.info("All owners are requested with the size: {}", owners.size());
        return owners;
        */
    }

    @Override
    public User signUp(UserDto userDto)
    {
        /*
         * AddressDto.builder()
                .country(userDto.getCountry())
                .city(userDto.getCity())
                .district(userDto.getDistrict())
                .street(userDto.getStreet())
                .build();
         */
        AddressDto addressDto = userDto.getAddress(); 
        List<CarDto> carDtos = userDto.getCars();

        User user;
        try
        {
            Address address = addressService.saveAddress(addressDto);
            if (address == null)
                throw new Exception();

            user = userRepository.save(convertToUser(userDto, address));

            if (carDtos != null) {
                /*
                for (CarDto carDto : carDtos) {
                    carService.saveCar(carDto, user.getId());
                }
                */
                carDtos.forEach(carDto -> {
                    carService.saveCar(carDto, user.getId());
                });
            }
            
            log.info("User {} is saved with mail: {}", userDto.getName(), userDto.getMail());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the user: {} with mail: {} error: {}", userDto.getName(), userDto.getMail(), ex.getMessage());
            return null;
        }
        return user;
    }

    @Override
    public UserDto signIn(String mail, String password)
    {
        User user = userRepository.findByMailAndPassword(mail, password);

        if (user == null) return null;

        log.info("User {} is logged in with mail: {}", user.getName(), user.getMail());
        return convertToUserDto(user);
    }

    @Override
    public void deleteUser(Long id){
        Optional<User> lotSummary = userRepository.findById(id);
        if (!lotSummary.isPresent()) throw new UserNotFoundException("User couldn't found by id: " + id);
        try{
            userRepository.delete(lotSummary.get());
            log.info("User with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the user, error: {}", ex.getMessage());
        }
    }

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .mail(user.getMail())
                .name(user.getName())
                .phone(user.getPhone())
                .type(user.getType())
                .address(addressService.convertToAddressDto(user.getAddress()))
                .cars(convertToCarDtos(userRepository.findCarsOfUser(user.getId()))) // to display cars
                .build();
    }

    private List<CarDto> convertToCarDtos(List<Car> cars) {
        if (cars == null || cars.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarDto> carDtos = new ArrayList<>();
        /* 
        for (Car car : cars) {
            CarDto carDto = carService.convertToCarDto(car);

            carDtos.add(carDto);
        }
        */

        cars.forEach(car -> {
            CarDto carDto = carService.convertToCarDto(car);
            carDtos.add(carDto);
        });
        
        return carDtos;
    }
    
    private User convertToUser(UserDto userDto, Address address) {
        return new User(null, userDto.getMail(), userDto.getName(),
                userDto.getPassword(), userDto.getPhone(),
                userDto.getType(), address, null);
    }

}
