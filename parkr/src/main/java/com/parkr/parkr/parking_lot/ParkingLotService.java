package com.parkr.parkr.parking_lot;

import com.google.gson.JsonElement;
import com.parkr.parkr.car.Car;
import com.parkr.parkr.car.CarRepository;
import com.parkr.parkr.common.GoogleServices;
import com.parkr.parkr.common.LocationModel;
import com.parkr.parkr.common.ParkingLotDetailModel;
import com.parkr.parkr.common.ParkingLotModel;
import com.parkr.parkr.lot_summary.LotSummaryDto;
import com.parkr.parkr.lot_summary.LotSummaryRepository;
import com.parkr.parkr.lot_summary.LotSummaryService;
import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingLotService implements IParkingLotService
{

    private final ParkingLotRepository parkingLotRepository;
    private final CarRepository carRepository;
    private final LotSummaryRepository lotSummaryRepository;
    private final LotSummaryService lotSummaryService;

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
            Integer lowestFare = null;
            String name = null;
            String image = null;
            if (parkingLotDB.isPresent()) {
                ParkingLot parkingLotEntity = parkingLotDB.get();
                name = parkingLotEntity.getName();
                image = parkingLotEntity.getPhotoUrl();
                capacity = parkingLotEntity.getCapacity();
                occupancy = parkingLotEntity.getOccupancy();
                String fares = parkingLotEntity.getFares();
                if (fares.equalsIgnoreCase("free")) {
                    // this is a free parking lot
                    lowestFare = 0;
                }
                else {
                    // fares should be in the correct JSONObject format in the database.
                    JSONObject faresJSON = new JSONObject(fares);
                    lowestFare = 1000;
                    for (String key : faresJSON.keySet()) {
                        Integer fare = faresJSON.optInt(key);
                        if (fare == 0) continue;
                        if (fare < lowestFare) {
                            lowestFare = fare;
                        }
                    }
                }
            }

            name = name == null ? parkingLotData.optString("name") : name;
            image = image == null ? "https://cdnuploads.aa.com.tr/uploads/Contents/2019/10/06/thumbs_b_c_0371b492b40dc268e6850ff2d1a9f968.jpg?v=134759" : image;
            Double rating = parkingLotData.optDouble("rating", 0.0);
            String status = parkingLotData.optString("business_status");
            int numOfRatings = parkingLotData.optInt("user_ratings_total", 0);
            JSONObject coordinates = parkingLotData.optJSONObject("geometry").optJSONObject("location");
            LocationModel location = new LocationModel();
            location.setLatitude(coordinates.optDouble("lat"));
            location.setLongitude(coordinates.optDouble("lng"));
            
            ParkingLotModel parkingLot = new ParkingLotModel();
            parkingLot.setName(name);
            parkingLot.setDistance(2.5);
            parkingLot.setStatus(status);
            parkingLot.setRating(rating);
            parkingLot.setNumOfRatings(numOfRatings);
            parkingLot.setPlaceID(placeID);
            parkingLot.setCoordinates(location);
            parkingLot.setCapacity(capacity);
            parkingLot.setOccupancy(occupancy);
            parkingLot.setLowestFare(lowestFare);
            parkingLot.setImage(image);
            
            parkingLotResponse.add(parkingLot);
        }

        return parkingLotResponse;
    }

    @Override
    public ParkingLotDetailModel getParkingLotByPlaceID(String placeID) {
        Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);

        Integer capacity = null, occupancy = null;
        JSONObject faresJSON = null;
        String name = null, image = null;
        String fares = null;
        if (parkingLotDB.isPresent()) {
            ParkingLot parkingLotEntity = parkingLotDB.get();
            name = parkingLotEntity.getName();
            image = parkingLotEntity.getPhotoUrl();
            capacity = parkingLotEntity.getCapacity();
            occupancy = parkingLotEntity.getOccupancy();
            fares = parkingLotEntity.getFares();
            if (fares.equalsIgnoreCase("free")) {
                faresJSON = new JSONObject();
            }
            else {
                faresJSON = new JSONObject(fares);
            }
        }

        JSONObject placeDetails = GoogleServices.getPlaceDetails(placeID).optJSONObject("result");
        if (placeDetails == null) {
            return null;
        }

        name = name == null ? placeDetails.optString("name") : name;
        image = image == null ? "https://cdnuploads.aa.com.tr/uploads/Contents/2019/10/06/thumbs_b_c_0371b492b40dc268e6850ff2d1a9f968.jpg?v=134759" : image;
        Double rating = placeDetails.optDouble("rating", 0.0);
        String status = placeDetails.optString("business_status");
        int numOfRatings = placeDetails.optInt("user_ratings_total", 0);
        JSONObject coordinates = placeDetails.optJSONObject("geometry").optJSONObject("location");

        HashMap<String, Object> faresMap = faresJSON == null ? null : (HashMap<String, Object>) faresJSON.toMap();
        if (faresMap != null && !faresMap.isEmpty()) {
            // since map and JSONObject are unordered types, we will sort the fares and send to the frontend.
            faresMap = sortFares(faresMap);
        }
        LocationModel location = new LocationModel();
        location.setLatitude(coordinates.optDouble("lat"));
        location.setLongitude(coordinates.optDouble("lng"));

        ParkingLotDetailModel parkingLotDetail = new ParkingLotDetailModel();
        parkingLotDetail.setName(name);
        parkingLotDetail.setStatus(status);
        parkingLotDetail.setRating(rating);
        parkingLotDetail.setNumOfRatings(numOfRatings);
        parkingLotDetail.setDistance(2.5);
        parkingLotDetail.setPlaceID(placeID);
        parkingLotDetail.setCoordinates(location);
        parkingLotDetail.setCapacity(capacity);
        parkingLotDetail.setOccupancy(occupancy);
        parkingLotDetail.setFares(faresMap == null ? null : faresMap);
        parkingLotDetail.setImage(image);

        return parkingLotDetail;
    }

    private LinkedHashMap<String, Object> sortFares(HashMap<String, Object> faresMap) {
        List<Map.Entry<String, Object>> list = new LinkedList<Map.Entry<String, Object>>(faresMap.entrySet());
    
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Object> >() {
            public int compare(Map.Entry<String, Object> o1,
                                Map.Entry<String, Object> o2)
            {
                return ((Integer) o1.getValue() < (Integer) o2.getValue() ? -1 : 1);
            }
        });

        // put data from sorted list to hashmap
        LinkedHashMap<String, Object> sortedFaresMap = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> fare : list) {
            sortedFaresMap.put(fare.getKey(), fare.getValue());
        }
        return sortedFaresMap;
    }

    @Override
    public void enterParkingLot(String plate, Long parkingLotID) {
        // insert a data to lot_summary
        // get the parking lot and the car from parkingLotID and from the plate.
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotID);
        if (!parkingLot.isPresent()) {
            log.error("Parking Lot with the id: {} could not be found", parkingLotID);
            return;
        }
        Optional<Car> car = carRepository.findByPlate(plate);
        if (!car.isPresent()) {
            log.error("Car with the plate: {} could not be found", plate);
            return;
        }

        // check if already exists
        Long lotSummaryID = lotSummaryRepository.getExistingLotSummary(car.get().getId(), parkingLotID);
        if (lotSummaryID != null) {
            log.error("Car with id {} is already in parking lot with id: {}", car.get().getId(), parkingLotID);
            return;
        }
        
        try {
            // increase the occupancy of parking lot by one
            parkingLotRepository.increaseParkingLotOccupancy(parkingLotID);
        } catch (Exception e) {
            log.error("Parking Lot occupany could not be increased. CarID: {}, parkingLotID: {}", car.get().getId(), parkingLotID);
        }

        // create a clock
        ZoneId zid = ZoneId.of("Europe/Istanbul");
        LocalDateTime lt = LocalDateTime.now(zid);

        LotSummaryDto lotSummaryDto = new LotSummaryDto();
        lotSummaryDto.setStartTime(lt);
        lotSummaryDto.setEndTime(null);
        lotSummaryDto.setFee(0);
        lotSummaryDto.setParkingLot(parkingLot.get());
        lotSummaryDto.setCar(car.get());

        try {
            lotSummaryService.saveLotSummary(lotSummaryDto);
            log.info("Car with plate {} has entered to parking lot with id {}", plate, parkingLotID);
        } catch (Exception e) {
            log.error("Error while Car with plate {} entering parking lot with id {}", plate, parkingLotID);
        }
    }

    @Override
    public void exitParkingLot(String plate, Long parkingLotID) {
        // update the end_time of lot summary entry.
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotID);
        if (!parkingLot.isPresent()) {
            log.error("Parking Lot with the id: {} could not be found", parkingLotID);
            return;
        }
        Optional<Car> car = carRepository.findByPlate(plate);
        if (!car.isPresent()) {
            log.error("Car with the plate: {} could not be found", plate);
            return;
        }

        Long lotSummaryID = lotSummaryRepository.getExistingLotSummary(car.get().getId(), parkingLotID);
        if (lotSummaryID == null) {
            log.error("Car with id {} is not in parking lot with id: {}", car.get().getId(), parkingLotID);
            return;
        }

        try {
            // decrease the occupancy of parking lot by one
            parkingLotRepository.decreaseParkingLotOccupancy(parkingLotID);
        } catch (Exception e) {
            log.error("Parking Lot occupany could not be decreased. CarID: {}, parkingLotID: {}", car.get().getId(), parkingLotID);
        }

        // create a clock
        ZoneId zid = ZoneId.of("Europe/Istanbul");
  
        // create a LocalDateTime object using now(zoneId)
        LocalDateTime lt = LocalDateTime.now(zid);
        try {
            lotSummaryRepository.updateEndTime(lt, car.get().getId());
        } catch (Exception e) {
            log.error("Error while Car with id {} exiting parking lot with id {}", car.get().getId(), parkingLotID);
        }
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
