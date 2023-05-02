package com.parkr.parkr.car;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.parkr.parkr.common.GoogleServices;
import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService implements ICarService
{
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public CarDto getCarById(Long id)
    {
        Optional<Car> car = carRepository.findById(id);

        if (car.isEmpty()) return null;

        log.info("Car with the id: {} is requested", id);

        return convertToCarDto(car.get());
    }

    @Override
    public List<CarDto> getAllCars()
    {
        List<CarDto> cars = carRepository.findAll().stream().map(this::convertToCarDto).toList();

        log.info("All cars are requested with the size: {}", cars.size());

        return cars;
    }

    @Override
    public Car saveCar(CarDto carDto, Long userId)
    {
        Car car;
        User user;
        try
        {
            user = userRepository.findById(userId).get();

            car = carRepository.save(convertToCar(carDto, user));
            log.info("Car is saved with id: {}", car.getId());
        }
        catch (Exception ex)
        {
            log.info("Error occurred while saving the car, error: {}", ex.getMessage());
            return null;
        }
        return car;
    }

    @Override
    public void deleteCar(Long id){
        Optional<Car> lotSummary = carRepository.findById(id);
        if (!lotSummary.isPresent()) throw new CarNotFoundException("Car couldn't found by id: " + id);
        try{
            carRepository.delete(lotSummary.get());
            log.info("Car with id is deleted: {}", id);
        }
        catch (Exception ex){
            log.info("Error occurred while deleting the car, error: {}", ex.getMessage());
        }
    }

    @Override
    public Double getFuelConsumptionInLiters(Double originLatitude, Double originLongitude, Double destinationLatitude, Double destinationLongitude, FuelType emissionType){
        JSONObject jsonResponse = GoogleServices.getEcoFriendlyRoute(originLatitude, originLongitude, destinationLatitude, destinationLongitude, emissionType.toString());
        JSONArray routes = jsonResponse.getJSONArray("routes");
        double fuelConsumptionMicroliters = -1;
    
        for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            JSONArray routeLabels = route.getJSONArray("routeLabels");
    
            for (int j = 0; j < routeLabels.length(); j++) {
                String label = routeLabels.getString(j);
    
                if ("FUEL_EFFICIENT".equals(label)) {
                    JSONObject travelAdvisory = route.getJSONObject("travelAdvisory");
                    fuelConsumptionMicroliters = travelAdvisory.getDouble("fuelConsumptionMicroliters");
                    break;
                }
            }
    
            if (fuelConsumptionMicroliters != -1) {
                break;
            }
        }
    
        if (fuelConsumptionMicroliters == -1) {
            throw new RuntimeException("FUEL_EFFICIENT route not found in the response");
        }
    
        // Convert microliters to liters
        double fuelConsumptionLiters = fuelConsumptionMicroliters / 1_000_000;
        return fuelConsumptionLiters;
    }

    public CarDto convertToCarDto(Car car) {
        return CarDto.builder()
                .id(car.getId())
                .plate(car.getPlate())
                .carType(car.getCarType())
                .model(car.getModel())
                .fuelType(car.getFuelType())
                .build();
    }

    private Car convertToCar(CarDto carDto, User user) {
        return new Car(null, carDto.getPlate(), carDto.getCarType(), carDto.getModel(), carDto.getFuelType(), user);
    }


/*
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
    */
}
