package com.parkr.parkr.address;

import java.util.List;

public interface IAddressService
{
    AddressDto getAddressById(Long id);

    List<AddressDto> getAllAddresses();

    Address saveAddress(AddressDto addressDto);

    void deleteAddress(Long id);

    AddressDto convertToAddressDto(Address address);
}
