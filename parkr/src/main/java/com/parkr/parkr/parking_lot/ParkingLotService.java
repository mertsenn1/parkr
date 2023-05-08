package com.parkr.parkr.parking_lot;

import com.google.gson.JsonElement;

import com.parkr.parkr.common.GoogleServices;
import com.parkr.parkr.common.LocationModel;
import com.parkr.parkr.common.ParkingLotDetailModel;
import com.parkr.parkr.common.ParkingLotModel;

import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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

    @Override
    public List<ParkingLotModel> getNearbyLots(Double latitude, Double longitude) {
        JSONObject parkingData = GoogleServices.crawlNearbyLots(latitude, longitude, "en");
        // if place id is in the database => return it from the database.
        // handle the output and return.
        JSONArray results = parkingData.getJSONArray("results");
        
        int size = results.length() > 10 ? 10 : results.length();
        ArrayList<ParkingLotModel> parkingLotResponse = new ArrayList<>();
        for ( int i = 0; i < size; i++) {
            JSONObject parkingLotData = results.getJSONObject(i);
            String placeID = parkingLotData.optString("place_id");
            // if place_id exists in the database => then fetch fares, occupancy and capacity and return.
            Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);
            Integer capacity = null, occupancy = null;
            Integer lowestFare = 1000; // for now 
            if (parkingLotDB.isPresent()) {
                ParkingLot parkingLotEntity = parkingLotDB.get();
                capacity = parkingLotEntity.getCapacity();
                occupancy = parkingLotEntity.getOccupancy();
                String fares = parkingLotEntity.getFares();
                JSONObject faresJSON = new JSONObject(fares);
                for (String key : faresJSON.keySet()) {
                    Integer fare = faresJSON.optInt(key);
                    if (fare < lowestFare) {
                        lowestFare = fare;
                    }
                }
            }

            String name = parkingLotData.optString("name");
            Double rating = parkingLotData.optDouble("rating", 0.0);
            JSONObject coordinates = parkingLotData.optJSONObject("geometry").optJSONObject("location");
            LocationModel location = new LocationModel();
            location.setLatitude(coordinates.optDouble("lat"));
            location.setLongitude(coordinates.optDouble("lng"));
            
            ParkingLotModel parkingLot = new ParkingLotModel();
            parkingLot.setName(name);
            parkingLot.setDistance(2.5);
            parkingLot.setRating(rating);
            parkingLot.setPlaceID(placeID);
            parkingLot.setCoordinates(location);
            parkingLot.setCapacity(capacity);
            parkingLot.setOccupancy(occupancy);
            parkingLot.setLowestFare(lowestFare);
            parkingLot.setImage("https://cdnuploads.aa.com.tr/uploads/Contents/2019/10/06/thumbs_b_c_0371b492b40dc268e6850ff2d1a9f968.jpg?v=134759");
            
            parkingLotResponse.add(parkingLot);

            /* 
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
            */
            
            //parkingLots.put(parkingLot);
        }

        return parkingLotResponse;
    }

    @Override
    public ParkingLotDetailModel getParkingLotByPlaceID(String placeID) {
        Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);

        Integer capacity = null, occupancy = null;
        JSONObject faresJSON = null;
        if (parkingLotDB.isPresent()) {
            ParkingLot parkingLotEntity = parkingLotDB.get();
            capacity = parkingLotEntity.getCapacity();
            occupancy = parkingLotEntity.getOccupancy();
            String fares = parkingLotEntity.getFares();
            faresJSON = new JSONObject(fares);
        }

        JSONObject placeDetails = GoogleServices.getPlaceDetails(placeID).optJSONObject("result");
        if (placeDetails == null) {
            return null;
        }

        String name = placeDetails.optString("name");
        Double rating = placeDetails.optDouble("rating", 0.0);
        JSONObject coordinates = placeDetails.optJSONObject("geometry").optJSONObject("location");

        LocationModel location = new LocationModel();
        location.setLatitude(coordinates.optDouble("lat"));
        location.setLongitude(coordinates.optDouble("lng"));

        ParkingLotDetailModel parkingLotDetail = new ParkingLotDetailModel();
        parkingLotDetail.setName(name);
        parkingLotDetail.setRating(rating);
        parkingLotDetail.setDistance(2.5);
        parkingLotDetail.setPlaceID(placeID);
        parkingLotDetail.setCoordinates(location);
        parkingLotDetail.setCapacity(capacity);
        parkingLotDetail.setOccupancy(occupancy);
        parkingLotDetail.setFares(faresJSON);
        parkingLotDetail.setImage("https://cdnuploads.aa.com.tr/uploads/Contents/2019/10/06/thumbs_b_c_0371b492b40dc268e6850ff2d1a9f968.jpg?v=134759");

        /* 
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
        */
        return parkingLotDetail;
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
