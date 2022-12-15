package com.parkr.parkr.location;

import java.util.List;

public interface ILocationService {
    LocationDto getLocationById(Long id);

    List<LocationDto> getAllLocations();

    Location saveLocation(LocationDto locationDto);

    LocationDto convertToLocationDto(Location location);
}
