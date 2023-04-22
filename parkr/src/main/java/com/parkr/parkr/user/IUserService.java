package com.parkr.parkr.user;

import java.util.List;

import com.parkr.parkr.auth.AuthenticationRequest;
import com.parkr.parkr.auth.AuthenticationResponse;

public interface IUserService
{
    UserDto getUserById(Long id);

    List<UserDto> getAllCustomers();

    List<UserDto> getAllOwners() ;

    AuthenticationResponse signUp(UserDto userDto);

    AuthenticationResponse signIn(AuthenticationRequest request);

    void deleteUser(Long id);
}
