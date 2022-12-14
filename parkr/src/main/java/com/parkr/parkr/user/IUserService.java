package com.parkr.parkr.user;

import java.util.List;

public interface IUserService
{
    UserDto getUserById(Long id);

    List<UserDto> getAllCustomers();

    List<UserDto> getAllOwners() ;

    User signUp(UserDto userDto);

    UserDto signIn(String mail, String password);
}
