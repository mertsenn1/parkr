package com.parkr.parkr.parking_lot;

import com.google.gson.JsonElement;
import com.parkr.parkr.address.Address;
import com.parkr.parkr.address.AddressDto;
import com.parkr.parkr.address.IAddressService;
import com.parkr.parkr.location.ILocationService;
import com.parkr.parkr.location.Location;
import com.parkr.parkr.location.LocationDto;
import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingLotService implements IParkingLotService
{

    private final LotCrawlerClient crawlerClient;
    private final ParkingLotRepository parkingLotRepository;
    private final UserRepository userRepository;

    private final IAddressService addressService;
    private final ILocationService locationService;

    public JsonElement getNearbyLots(Double latitude, Double longitude, String language, Integer maxPrice,
                                     Integer minPrice, Boolean openNow, Integer radius, String type) {
        // Set default values if not specified
        if (language == null) {
            language = "en";
        }
        if (maxPrice == null) {
            maxPrice = 4;
        }

        if (minPrice == null) {
            minPrice = 0;
        }

        if (openNow == null) {
            openNow = false;
        }

        if (radius == null) {
            radius = 50000;
        }

        if (type == null) {
            type = "parking";
        }

        return crawlerClient.crawlNearbyLots(latitude, longitude, language, maxPrice, minPrice, openNow, radius, type);
    }

    @Override
    public ParkingLotDto getParkingLotById(Long id) {
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(id);

        if (!parkingLot.isPresent()) return null;

        log.info("Parking Lot with the id: {} is requested", id);

        return convertToParkingLotDto(parkingLot.get());
    }

    @Override
    public List<ParkingLotDto> getAllParkingLots() {
        List<ParkingLotDto> parkingLots = parkingLotRepository.findAll().stream().map(this::convertToParkingLotDto).toList();

        log.info("All parking lots are requested with the size: {}", parkingLots.size());

        return parkingLots;
    }

    @Override
    public ParkingLot saveParkingLot(ParkingLotDto parkingLotDto, Long ownerId) {
        ParkingLot parkingLot;
        LocationDto locationDto;
        AddressDto addressDto;
        User owner;
        
        locationDto = parkingLotDto.getLocation();
        addressDto = parkingLotDto.getAddress();

        try
        {
            owner = userRepository.findById(ownerId).get();
            Address address = addressService.saveAddress(addressDto);
            if (address == null)
                throw new Exception();

            Location location = locationService.saveLocation(locationDto);
            if (location == null)
                throw new Exception();

            parkingLot = parkingLotRepository.save(convertToParkingLot(parkingLotDto, address, location, owner));
            log.info("Parking Lot is saved with id: {}", parkingLot.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the parking lot, error: {}", ex.getMessage());
            return null;
        }
        return parkingLot;
    }

    @Override
    public void deleteParkingLot(Long id){
        Optional<ParkingLot> lotSummary = parkingLotRepository.findById(id);
        if (!lotSummary.isPresent()) throw new ParkingLotNotFoundException("ParkingLot couldn't found by id: " + id);
        try{
            parkingLotRepository.delete(lotSummary.get());
            log.info("ParkingLot with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the parkingLot, error: {}", ex.getMessage());
        }
    }

    private ParkingLotDto convertToParkingLotDto(ParkingLot parkingLot) {
        return ParkingLotDto.builder()
                .id(parkingLot.getId())
                .name(parkingLot.getName())
                .address(addressService.convertToAddressDto(parkingLot.getAddress()))
                .ownerId(parkingLot.getOwner().getId())
                .location(locationService.convertToLocationDto(parkingLot.getLocation()))
                .priceLevel(parkingLot.getPriceLevel())
                .photoUrl(parkingLot.getPhotoUrl())
                .status(parkingLot.getStatus())
                .capacity(parkingLot.getCapacity())
                .occupancy(parkingLot.getOccupancy())
                .build();
    }

    private ParkingLot convertToParkingLot(ParkingLotDto parkingLotDto, Address address, Location location, User owner) {
        return new ParkingLot(null, parkingLotDto.getName(),
                    address, owner, location, parkingLotDto.getPriceLevel(), parkingLotDto.getPhotoUrl(), 
                    parkingLotDto.getStatus(), parkingLotDto.getCapacity(), parkingLotDto.getOccupancy());
    }
}
