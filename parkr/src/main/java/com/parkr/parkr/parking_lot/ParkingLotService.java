package com.parkr.parkr.parking_lot;

import com.google.gson.JsonElement;
import com.parkr.parkr.address.Address;
import com.parkr.parkr.address.AddressDto;
import com.parkr.parkr.address.IAddressService;
import com.parkr.parkr.common.GoogleServices;
import com.parkr.parkr.location.ILocationService;
import com.parkr.parkr.location.Location;
import com.parkr.parkr.location.LocationDto;
import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingLotService implements IParkingLotService
{

    private final ParkingLotRepository parkingLotRepository;
    private final UserRepository userRepository;

    private final IAddressService addressService;
    private final ILocationService locationService;

    @Override
    public JSONArray getNearbyLots(Double latitude, Double longitude, String language) {
        // Set default values if not specified
        if (language == null) {
            language = "en";
        }

        JSONObject parkingData = GoogleServices.crawlNearbyLots(latitude, longitude, language);
        // if place id is in the database => return it from the database.
        // handle the output and return.
        JSONArray results = parkingData.getJSONArray("results");
        
        JSONArray parkingLots = new JSONArray();
        int size = results.length() > 10 ? 10 : results.length();
        for ( int i = 0; i < size; i++) {
            JSONObject parkingLotData = results.getJSONObject(i);
            String placeID = parkingLotData.optString("place_id");
            // if place_id exists in the database => then fetch fares, occupancy and capacity and return.
            Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);
            int capacity, occupancy;
            Double lowestFare = 1000.0; // for now 
            if (parkingLotDB.isPresent()) {
                ParkingLot parkingLotEntity = parkingLotDB.get();
                capacity = parkingLotEntity.getCapacity();
                occupancy = parkingLotEntity.getOccupancy();
                String fares = parkingLotEntity.getFares();
                JSONObject faresJSON = new JSONObject(fares);
                for (String key : faresJSON.keySet()) {
                    Double fare = faresJSON.optDouble(key);
                    if (fare < lowestFare) {
                        lowestFare = fare;
                    }
                }
            }
            else {
                capacity = 0;
                occupancy = 0;
            }

            JSONObject parkingLot = new JSONObject();
            String name = parkingLotData.optString("name");
            Double rating = parkingLotData.optDouble("rating", 0.0);
            JSONObject coordinates = parkingLotData.optJSONObject("geometry").optJSONObject("location");
            parkingLot.put("name", name);
            parkingLot.put("rating", rating);
            parkingLot.put("distance", 2.5);
            parkingLot.put("placeID", placeID);
            parkingLot.put("coordinates", coordinates);
            parkingLot.put("capacity", capacity);
            parkingLot.put("occupancy", occupancy);
            parkingLot.put("lowestFare", lowestFare);
            // photo will be fetched later from google photo place api.
            parkingLot.put("image", "https://cdnuploads.aa.com.tr/uploads/Contents/2019/10/06/thumbs_b_c_0371b492b40dc268e6850ff2d1a9f968.jpg?v=134759");
            
            
            parkingLots.put(parkingLot);
        }

        return parkingLots;
    }

    @Override
    public JSONObject getParkingLotByPlaceID(String placeID) {
        Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);

        int capacity, occupancy;
        JSONObject faresJSON = new JSONObject();
        if (parkingLotDB.isPresent()) {
            ParkingLot parkingLotEntity = parkingLotDB.get();
            capacity = parkingLotEntity.getCapacity();
            occupancy = parkingLotEntity.getOccupancy();
            String fares = parkingLotEntity.getFares();
            faresJSON = new JSONObject(fares);
        }
        else {
            capacity = 0;
            occupancy = 0;
        }

        JSONObject placeDetails = GoogleServices.getPlaceDetails(placeID).getJSONObject("result");

        JSONObject parkingLot = new JSONObject();
        String name = placeDetails.optString("name");
        Double rating = placeDetails.optDouble("rating", 0.0);
        JSONObject coordinates = placeDetails.optJSONObject("geometry").optJSONObject("location");

        parkingLot.put("name", name);
        parkingLot.put("rating", rating);
        parkingLot.put("distance", 2.5);
        parkingLot.put("placeID", placeID);
        parkingLot.put("coordinates", coordinates);
        parkingLot.put("capacity", capacity);
        parkingLot.put("occupancy", occupancy);
        parkingLot.put("fares", faresJSON);
        // photo will be fetched later from google photo place api.
        parkingLot.put("image", "https://cdnuploads.aa.com.tr/uploads/Contents/2019/10/06/thumbs_b_c_0371b492b40dc268e6850ff2d1a9f968.jpg?v=134759");

        return parkingLot;
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
    public ParkingLot saveParkingLot(ParkingLotDto parkingLotDto) {
        return null;
        /* 
        ParkingLot parkingLot;
        LocationDto locationDto;
        AddressDto addressDto;
        User owner;

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
        */
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
               // .address(addressService.convertToAddressDto(parkingLot.getAddress()))
               // .ownerId(parkingLot.getOwner().getId())
               // .location(locationService.convertToLocationDto(parkingLot.getLocation()))
               // .priceLevel(parkingLot.getPriceLevel())
                .photoUrl(parkingLot.getPhotoUrl())
                .status(parkingLot.getStatus())
                .capacity(parkingLot.getCapacity())
                .occupancy(parkingLot.getOccupancy())
                .build();
    }

    private ParkingLot convertToParkingLot(ParkingLotDto parkingLotDto) {
        return null;
        /* 
        return new ParkingLot(null, parkingLotDto.getName(),
                    parkingLotDto.getPhotoUrl(), 
                    parkingLotDto.getStatus(), parkingLotDto.getCapacity(), parkingLotDto.getOccupancy());
                    */
    }
}