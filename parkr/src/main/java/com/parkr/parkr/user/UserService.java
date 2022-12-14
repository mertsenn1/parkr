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
        List<UserDto> customers = userRepository.findAllByIsOwnerIsFalse().stream().map(this::convertToUserDto).toList();
        log.info("All customers are requested with the size: {}", customers.size());
        return customers;
    }

    @Override
    public List<UserDto> getAllOwners()
    {
        List<UserDto> owners = userRepository.findAllByIsOwnerIsTrue().stream().map(this::convertToUserDto).toList();
        log.info("All owners are requested with the size: {}", owners.size());
        return owners;
    }

    @Override
    public User signUp(UserDto userDto)
    {
        AddressDto addressDto = AddressDto.builder()
                .country(userDto.getCountry())
                .city(userDto.getCity())
                .district(userDto.getDistrict())
                .street(userDto.getStreet())
                .build();
        CarDto carDto = CarDto.builder()
                .plate(userDto.getPlate())
                .carType(userDto.getCarType())
                .model(userDto.getModel())
                .fuelType(userDto.getFuelType())
                .build();
        User user;
        try
        {
            Address address = addressService.saveAddress(addressDto);
            Car car = carService.saveCar(carDto);
            user = userRepository.save(convertToUser(userDto, address, car));
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

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .mail(user.getMail())
                .name(user.getName())
                .phone(user.getPhone())
                .isOwner(user.getIsOwner())
                .street(user.getAddress().getStreet())
                .country(user.getAddress().getCountry())
                .district(user.getAddress().getDistrict())
                .city(user.getAddress().getCity())
                .password(user.getPassword())
                .plate(user.getCar().getPlate())
                .model(user.getCar().getModel())
                .fuelType(user.getCar().getFuelType())
                .carType(user.getCar().getCarType())
                .build();
    }

    private User convertToUser(UserDto userDto, Address address, Car car) {
        return new User(null, userDto.getMail(), userDto.getName(),
                userDto.getPassword(), userDto.getPhone(),
                userDto.getIsOwner(), address, car);
    }

}
