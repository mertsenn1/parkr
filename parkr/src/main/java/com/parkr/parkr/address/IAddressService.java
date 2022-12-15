package com.parkr.parkr.address;

import java.util.List;

public interface IAddressService
{
    AddressDto getAddressById(Long id);

    List<AddressDto> getAllAddresses();

    Address saveAddress(AddressDto addressDto);

    AddressDto convertToAddressDto(Address address);
}
