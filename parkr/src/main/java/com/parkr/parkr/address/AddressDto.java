package com.parkr.parkr.address;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto
{
    private Long id;
    
    private String country;

    private String city;

    private String district;

    private String street;
}
