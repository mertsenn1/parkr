package com.parkr.parkr.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService implements ILocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationDto getLocationById(Long id) {
        Optional<Location> location = locationRepository.findById(id);

        if (!location.isPresent()) return null;

        log.info("Location with the id: {} is requested", id);

        return convertToLocationDto(location.get());
    }

    @Override
    public List<LocationDto> getAllLocations() {
        List<LocationDto> locations = locationRepository.findAll().stream().map(this::convertToLocationDto).toList();

        log.info("All locations are requested with the size: {}", locations.size());

        return locations;
    }

    @Override
    public Location saveLocation(LocationDto locationDto) {
        Location location;
        try
        {
            location = locationRepository.save(convertToLocation(locationDto));
            log.info("Location is saved with id: {}", location.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the location, error: {}", ex.getMessage());
            return null;
        }
        return location;
    }

    public void deleteLocation(Long id){
        Optional<Location> lotSummary = locationRepository.findById(id);
        if (!lotSummary.isPresent()) throw new LocationNotFoundException("Location couldn't found by id: " + id);
        try{
            locationRepository.delete(lotSummary.get());
            log.info("Location with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the location, error: {}", ex.getMessage());
        }
    } 

    @Override
    public LocationDto convertToLocationDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    private Location convertToLocation(LocationDto locationDto)
    {
        return new Location(null, locationDto.getLat(), locationDto.getLon());
    }


}
