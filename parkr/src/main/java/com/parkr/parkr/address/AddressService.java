package com.parkr.parkr.address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService
{
    private final AddressRepository addressRepository;
    @Override
    public AddressDto getAddressById(Long id)
    {
        Optional<Address> address = addressRepository.findById(id);

        if (address.isEmpty()) return null;

        log.info("Address with the id: {} is requested", id);

        return convertToAddressDto(address.get());
    }

    @Override
    public List<AddressDto> getAllAddresses()
    {
        List<AddressDto> addresses = addressRepository.findAll().stream().map(this::convertToAddressDto).toList();

        log.info("All addresses are requested with the size: {}", addresses.size());

        return addresses;
    }

    @Override
    public Address saveAddress(AddressDto addressDto)
    {
        Address address;
        try
        {
            address = addressRepository.save(convertToAddress(addressDto));
            log.info("Address is saved with id: {}", address.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the address, error: {}", ex.getMessage());
            return null;
        }
        return address;
    }

    private Address convertToAddress(AddressDto addressDto)
    {
        return new Address(null, addressDto.getCountry(), addressDto.getCity(),
                addressDto.getDistrict(), addressDto.getStreet());
    }

    public AddressDto convertToAddressDto(Address address)
    {
        return AddressDto.builder()
                .id(address.getId())
                .country(address.getCountry())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .build();
    }
}
