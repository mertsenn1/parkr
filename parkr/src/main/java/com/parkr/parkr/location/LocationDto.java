package com.parkr.parkr.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
    private Long id;
    
    private String lat;

    private String lon;
}
