package com.parkr.parkr.user;

import com.parkr.parkr.car.CarType;
import com.parkr.parkr.car.FuelType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto
{
    private String mail;

    private String name;

    private String password;

    private String phone;

    private Boolean isOwner;

    private String country;

    private String city;

    private String district;

    private String street;

    private String plate;

    private CarType carType;

    private String model;

    private FuelType fuelType;
}
