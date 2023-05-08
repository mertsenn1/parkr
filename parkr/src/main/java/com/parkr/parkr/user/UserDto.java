package com.parkr.parkr.user;

import java.util.List;

import com.parkr.parkr.car.CarDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto
{
    private Long id;
    
    private String mail;

    private String name;

    private String password;

    private String phone;

    private String type;

    private List<CarDto> cars;

    private Role role;
    /* 
    private String country;

    private String city;

    private String district;

    private String street;

    private String plate;

    private CarType carType;

    private String model;

    private FuelType fuelType;
    */
}
