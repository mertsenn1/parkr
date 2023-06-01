package com.parkr.parkr.parking_lot;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.parkr.parkr.car.Car;
import com.parkr.parkr.car.CarDto;
import com.parkr.parkr.car.CarRepository;
import com.parkr.parkr.car.CarType;
import com.parkr.parkr.car.FuelType;
import com.parkr.parkr.common.GoogleServices;
import com.parkr.parkr.common.LocationModel;
import com.parkr.parkr.common.LotActivityModel;
import com.parkr.parkr.common.ParkingLotDetailModel;
import com.parkr.parkr.common.ParkingLotModel;
import com.parkr.parkr.common.RouteDetailsModel;
import com.parkr.parkr.lot_summary.LotSummary;
import com.parkr.parkr.lot_summary.LotSummaryDto;
import com.parkr.parkr.lot_summary.LotSummaryRepository;
import com.parkr.parkr.lot_summary.LotSummaryService;
import com.parkr.parkr.user.IUserService;
import com.parkr.parkr.user.User;
import com.parkr.parkr.user.UserRepository;
import com.parkr.parkr.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParkingLotService implements IParkingLotService
{

    private ParkingLotRepository parkingLotRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    private LotSummaryRepository lotSummaryRepository;
    private LotSummaryService lotSummaryService;
    private IUserService userService;
    private Cache<String, String> cache;

    @Autowired
    public ParkingLotService(@Lazy IUserService userService, ParkingLotRepository parkingLotRepository, UserRepository userRepository, LotSummaryService lotSummaryService, LotSummaryRepository lotSummaryRepository,
                                CarRepository carRepository) {
        this.userService = userService;
        this.lotSummaryRepository = lotSummaryRepository;
        this.lotSummaryService = lotSummaryService;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.cache = Caffeine.newBuilder()
        .expireAfterWrite(15, TimeUnit.SECONDS)
        .build();
    }

    @Override
    public List<ParkingLotModel> getNearbyLots(Double latitude, Double longitude) {
        JSONObject parkingData = GoogleServices.crawlNearbyLots(latitude, longitude, "en");
        // if place id is in the database => return it from the database.
        // handle the output and return.
        JSONArray results = parkingData.getJSONArray("results");
        
        int size = results.length() > 10 ? 10 : results.length();
        ArrayList<ParkingLotModel> parkingLotResponse = new ArrayList<>();
        List<String> destinations = new ArrayList<>();
        for ( int i = 0; i < size; i++) {
            JSONObject parkingLotData = results.getJSONObject(i);
            String placeID = parkingLotData.optString("place_id");
            destinations.add(placeID);
            // if place_id exists in the database => then fetch fares, occupancy and capacity and return.
            Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);
            Integer capacity = null, occupancy = null;
            Integer lowestFare = null;
            String name = null;
            String image = null;
            boolean hasAggreement = false;
            if (parkingLotDB.isPresent()) {
                ParkingLot parkingLotEntity = parkingLotDB.get();
                name = parkingLotEntity.getName();
                image = parkingLotEntity.getPhotoUrl();
                capacity = parkingLotEntity.getCapacity();
                occupancy = parkingLotEntity.getOccupancy();
                hasAggreement = true;
                String fares = parkingLotEntity.getFares();
                if (fares.equalsIgnoreCase("free")) {
                    // this is a free parking lot
                    lowestFare = 0;
                }
                else {
                    // fares should be in the correct JSONObject format in the database.
                    JSONObject faresJSON = new JSONObject(fares).getJSONObject("fares");
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
            image = image == null ? "https://media.istockphoto.com/id/1324853440/photo/parking-lot-in-public-areas.jpg?b=1&s=170667a&w=0&k=20&c=Y4f2QhvXJKwI9-hoaiPCvn_EQPZ2F_AQ03oNv4-3SlE=" : image;
            Double rating = parkingLotData.optDouble("rating", 0.0);
            String status = parkingLotData.optString("business_status", "OPERATIONAL");
            int numOfRatings = parkingLotData.optInt("user_ratings_total", 0);
            JSONObject coordinates = parkingLotData.optJSONObject("geometry").optJSONObject("location");
            LocationModel location = new LocationModel();
            location.setLatitude(coordinates.optDouble("lat"));
            location.setLongitude(coordinates.optDouble("lng"));
            
            ParkingLotModel parkingLot = new ParkingLotModel();
            parkingLot.setName(name);
            parkingLot.setStatus(status);
            parkingLot.setRating(rating);
            parkingLot.setNumOfRatings(numOfRatings);
            parkingLot.setPlaceID(placeID);
            parkingLot.setCoordinates(location);
            parkingLot.setCapacity(capacity);
            parkingLot.setOccupancy(occupancy);
            parkingLot.setLowestFare(lowestFare);
            parkingLot.setImage(image);
            parkingLot.setHasAggreement(hasAggreement);
            
            parkingLotResponse.add(parkingLot);
        }

        JSONArray routeDistances;
        try {
            routeDistances = GoogleServices.getRouteDistances(latitude, longitude, destinations);
        } catch (Exception e) {
            routeDistances = null;
        }

        // finding the distances of parking lots to the user's location.
        for (int i = 0; i < parkingLotResponse.size(); i++) {
            Double distance = 0.0;
            if (routeDistances != null) {
                for ( int j = 0; j < routeDistances.length(); j++) {
                    JSONObject routeDistance = routeDistances.getJSONObject(j);
                    if (routeDistance.getInt("destinationIndex") == i) {
                        distance = routeDistance.optDouble("distanceMeters", 0.0);
                    }
                }
            }
            parkingLotResponse.get(i).setDistance(distance / 1000.0); // as km
        }

        Collections.sort(parkingLotResponse, Comparator.comparing(ParkingLotModel::getDistance));
        return parkingLotResponse;
    }

    @Override
    public ParkingLotDetailModel getParkingLotByPlaceID(String placeID) {
        Optional<ParkingLot> parkingLotDB = parkingLotRepository.findByPlaceId(placeID);

        Integer capacity = null, occupancy = null;
        JSONObject faresJSON = null;
        String name = null, image = null;
        String fares = null;
        boolean hasAggreement = false;
        Integer lowestFare = null;
        if (parkingLotDB.isPresent()) {
            ParkingLot parkingLotEntity = parkingLotDB.get();
            name = parkingLotEntity.getName();
            image = parkingLotEntity.getPhotoUrl();
            capacity = parkingLotEntity.getCapacity();
            occupancy = parkingLotEntity.getOccupancy();
            hasAggreement = true;
            fares = parkingLotEntity.getFares();
            if (fares.equalsIgnoreCase("free")) {
                faresJSON = new JSONObject();
                lowestFare = 0;
            }
            else {
                faresJSON = new JSONObject(fares).getJSONObject("fares");
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

        JSONObject placeDetails = GoogleServices.getPlaceDetails(placeID).optJSONObject("result");
        if (placeDetails == null) {
            return null;
        }

        name = name == null ? placeDetails.optString("name") : name;
        image = image == null ? "https://media.istockphoto.com/id/1324853440/photo/parking-lot-in-public-areas.jpg?b=1&s=170667a&w=0&k=20&c=Y4f2QhvXJKwI9-hoaiPCvn_EQPZ2F_AQ03oNv4-3SlE=" : image;
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
        parkingLotDetail.setPlaceID(placeID);
        parkingLotDetail.setCoordinates(location);
        parkingLotDetail.setCapacity(capacity);
        parkingLotDetail.setOccupancy(occupancy);
        parkingLotDetail.setFares(faresMap == null ? null : faresMap);
        parkingLotDetail.setImage(image);
        parkingLotDetail.setHasAggreement(hasAggreement);
        parkingLotDetail.setLowestFare(lowestFare);

        return parkingLotDetail;
    }

    public HashMap<String, Object> getParkingLotFares(){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // get the parking lot of the user
        ParkingLot parkingLot = currentUser.getParkingLots().get(0);
        String fares = parkingLot.getFares();
        if (fares.equalsIgnoreCase("free")) {
            return new HashMap<String, Object>();
        }
        else {
            HashMap<String, Object> faresMap = new HashMap<>();
            JSONObject parentJSON = new JSONObject(fares);
            JSONObject faresJSON = parentJSON.getJSONObject("fares");
            faresMap = sortFares((HashMap<String, Object>) faresJSON.toMap());
            HashMap<String,Object> parentMap = new HashMap<>();
            parentMap.put("fares", faresMap);
            parentMap.put("freeMinutes", parentJSON.optInt("freeMinutes", 0));
            return parentMap;
        }
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

    private String adjustLicensePlate(String plate) {
        String adjustedPlate = plate.replaceAll("\\s", ""); // remove spaces.
        adjustedPlate = adjustedPlate.toUpperCase();

        String pattern = "^[0-9]{2}[A-Z]{1,3}[0-9]{2,5}$"; // regex for turkish license plates. 52AB123
        if (adjustedPlate.matches(pattern)) {
            log.info("The given plate {} => returned as {} is valid!", plate, adjustedPlate);
            return adjustedPlate;
        } else {
            adjustedPlate = adjustedPlate.replaceAll("[$]", "S");
            adjustedPlate = adjustedPlate.replaceAll("[€]", "E");
            adjustedPlate = adjustedPlate.replaceAll("[@]", "A");
            adjustedPlate = adjustedPlate.replaceAll("[&]", "8");
            adjustedPlate = adjustedPlate.replaceAll("[%]", "5");
            log.error("The given plate {} was invalid => returned {}!", plate, adjustedPlate);
            return adjustedPlate; // return anyways.
        }
    }

    @Override
    public void enterParkingLot(String plate, Long parkingLotID) {
        log.info("Enter parking lot is called with plate: {}, and parkingLotID: {}!", plate, parkingLotID);
        plate = adjustLicensePlate(plate);
        
        try {
            String cached = cache.getIfPresent(plate); // check if the plate is in the cache.
            if (cached != null && cached.equals("entry")) {
                log.error("The car with the plate {} is found in the cache in entry.", plate);
                return;
            } else {
                // The license plate has not been processed within the last 15 seconds, so process it
                // Store the license plate in the cache
                cache.put(plate, "entry");
            }
        } catch (Exception e) {
            log.error("Cache error!");
        }

        // insert a data to lot_summary
        // get the parking lot and the car from parkingLotID and from the plate.
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotID);
        if (!parkingLot.isPresent()) {
            log.error("Parking Lot with the id: {} could not be found", parkingLotID);
            return;
        }
        Optional<Car> car = carRepository.findByPlate(plate);
        if (car.isPresent()) {
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

            //LocalDateTime specificDate = LocalDateTime.of(2023, Month.MAY, 22, 17, 01, 15);
            LotSummaryDto lotSummaryDto = new LotSummaryDto();
            lotSummaryDto.setStartTime(lt);
            lotSummaryDto.setEndTime(null);
            lotSummaryDto.setFee(0);
            lotSummaryDto.setParkingLot(parkingLot.get());
            lotSummaryDto.setCar(car.get());
            lotSummaryDto.setPaidAmount(0);
            lotSummaryDto.setStatus("Unpaid");

            try {
                lotSummaryService.saveLotSummary(lotSummaryDto);
                log.info("Car with plate {} has entered to parking lot with id {}", plate, parkingLotID);
            } catch (Exception e) {
                log.error("Error while Car with plate {} entering parking lot with id {}", plate, parkingLotID);
            }
        }
        else {
            log.info("Car with the plate: {} is not in the database!", plate);
            try {
                parkingLotRepository.increaseParkingLotOccupancy(parkingLotID);
                Car newCar = new Car();
                newCar.setCarType(CarType.valueOf("UNKNOWN"));
                newCar.setFuelType(FuelType.valueOf("UNKNOWN"));
                newCar.setModel("UNKNOWN");
                newCar.setUser(null);
                newCar.setPlate(plate);

                Car savedCar = carRepository.save(newCar);
                // increase the occupancy of parking lot by one

                ZoneId zid = ZoneId.of("Europe/Istanbul");
                LocalDateTime lt = LocalDateTime.now(zid);
    
                //LocalDateTime specificDate = LocalDateTime.of(2023, Month.MAY, 22, 17, 01, 15);
                LotSummaryDto lotSummaryDto = new LotSummaryDto();
                lotSummaryDto.setStartTime(lt);
                lotSummaryDto.setEndTime(null);
                lotSummaryDto.setFee(0);
                lotSummaryDto.setParkingLot(parkingLot.get());
                lotSummaryDto.setCar(savedCar);
                lotSummaryDto.setPaidAmount(0);
                lotSummaryDto.setStatus("Unpaid");
                
                try {
                    lotSummaryService.saveLotSummary(lotSummaryDto);
                    log.info("Car with plate {} has entered to parking lot with id {}", plate, parkingLotID);
                } catch (Exception e) {
                    log.error("Error while Car with plate {} entering parking lot with id {}", plate, parkingLotID);
                }
            } catch (Exception e) {
                log.error("Parking Lot occupany could not be increased. CarID: {}, parkingLotID: {}", car.get().getId(), parkingLotID);
            }
        }
    }

    @Override
    public void exitParkingLot(String plate, Long parkingLotID) {
        log.info("Exit parking lot is called with plate: {}, and parkingLotID: {}!", plate, parkingLotID);
        plate = adjustLicensePlate(plate);

        try {
            String cached = cache.getIfPresent(plate); // check if the plate is in the cache.
            if (cached != null && cached.equals("exit")) {
                log.error("The car with the plate {} is found in the cache in exit.", plate);
                return;
            } else {
                // The license plate has not been processed within the last 15 seconds, so process it
                // Store the license plate in the cache
                cache.put(plate, "exit");
            }
        } catch (Exception e) {
            log.error("Cache error!");
        }

        // update the end_time of lot summary entry.
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotID);
        if (!parkingLot.isPresent()) {
            log.error("Parking Lot with the id: {} could not be found", parkingLotID);
            return;
        }
        Optional<Car> car = carRepository.findByPlate(plate);
        if (car.isPresent()) {
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
                Integer fee = userService.calculateCurrentFee(car.get().getId());
                lotSummaryRepository.updateLotSummary(lt, fee, car.get().getId());
            } catch (Exception e) {
                log.error("Error while Car with id {} exiting parking lot with id {}", car.get().getId(), parkingLotID);
            }
        }
        else {
            log.info("Car with the plate: {} is not in the database!", plate);
            try {
                // decrease the occupancy of parking lot by one
                parkingLotRepository.decreaseParkingLotOccupancy(parkingLotID);
            } catch (Exception e) {
                log.error("Parking Lot occupany could not be decreased. CarID: {}, parkingLotID: {}", car.get().getId(), parkingLotID);
            }
        }
    }

    @Override
    public List<LotActivityModel> getCurrentLotActivities() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<LotSummary> summaryList = lotSummaryRepository.getCurrentLotSummariesOfParkingLot(currentUser.getParkingLots().get(0).getId());

        ArrayList<LotActivityModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            LotActivityModel responseModel = new LotActivityModel();
            responseModel.setId(summary.getId());
            responseModel.setLicensePlate(summary.getCar().getPlate());
            responseModel.setFee(userService.calculateCurrentFee(summary.getCar().getId()));
            responseModel.setStartTime(summary.getStartTime());
            responseModel.setCarType(summary.getCar().getCarType());
            responseModel.setStatus(summary.getStatus());
            responseModel.setLastPaidTime(summary.getLastPaidTime());
            responseModel.setPaidAmount(summary.getPaidAmount());

            responseList.add(responseModel);
        });
        return responseList;
    }

    @Override
    public List<LotActivityModel> getPastLotActivities() {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<LotSummary> summaryList = lotSummaryRepository.getPastLotSummariesOfParkingLot(currentUser.getParkingLots().get(0).getId());

        ArrayList<LotActivityModel> responseList = new ArrayList<>();
        summaryList.forEach(summary -> {
            LotActivityModel responseModel = new LotActivityModel();
            responseModel.setId(summary.getId());
            responseModel.setLicensePlate(summary.getCar().getPlate());
            responseModel.setFee(summary.getFee());
            responseModel.setStartTime(summary.getStartTime());
            responseModel.setEndTime(summary.getEndTime());
            responseModel.setCarType(summary.getCar().getCarType());
            responseModel.setStatus(summary.getStatus());
            responseModel.setLastPaidTime(summary.getLastPaidTime());
            responseModel.setPaidAmount(summary.getPaidAmount());

            responseList.add(responseModel);
        });
        return responseList;
    }

    @Override
    public List<RouteDetailsModel> getRouteDetails(Double originLatitude, Double originLongitude, String destinationPlaceID, Long carID){
        Optional<Car> car = carRepository.findById(carID);
        FuelType fuelType = null;
        if (!car.isPresent()) {
            log.error("Car could not be found with the id: {}", carID);
            fuelType = FuelType.valueOf("DIESEL"); // default
        }
        else {
            fuelType = car.get().getFuelType();
        }
        JSONObject jsonResponse = GoogleServices.getEcoFriendlyRoute(originLatitude, originLongitude, destinationPlaceID, fuelType.toString());
        JSONArray routes = jsonResponse.getJSONArray("routes");
    
        boolean ecoFriendlyFound = false;
        boolean defaultRouteFound = false;
        boolean bothRoutesSame = true;
        JSONObject route = new JSONObject();
        JSONObject ecoFriendlyRoute = new JSONObject();
        JSONObject defaultRoute = new JSONObject();
        for (int i = 0; i < routes.length(); i++) {
            route = routes.getJSONObject(i);
            JSONArray routeLabels = route.getJSONArray("routeLabels");
    
            for (int j = 0; j < routeLabels.length(); j++) {
                String label = routeLabels.getString(j);
    
                if ("DEFAULT_ROUTE".equals(label)) {
                    defaultRouteFound = true;
                    defaultRoute = route;
                }
                else if ("FUEL_EFFICIENT".equals(label)) {
                    ecoFriendlyFound = true;
                    ecoFriendlyRoute = route;
                }
            }
            
            if (defaultRouteFound && !ecoFriendlyFound) {
                bothRoutesSame = false;
            }
            else if (!defaultRouteFound && ecoFriendlyFound) {
                bothRoutesSame = false;
            }
            else if (ecoFriendlyFound && defaultRouteFound) {
                break;
            }
        }
    
        if (!ecoFriendlyFound) {
            throw new RuntimeException("FUEL_EFFICIENT route not found in the response");
        }

        List<RouteDetailsModel> routeList = new ArrayList<>();

        RouteDetailsModel ecoFriendlyrouteDetail = new RouteDetailsModel();
        // Convert microliters to liters
        JSONObject travelAdvisory = ecoFriendlyRoute.getJSONObject("travelAdvisory");
        Double fuelConsumptionMicroliters = travelAdvisory.getDouble("fuelConsumptionMicroliters");
        Double fuelConsumptionLiters = fuelConsumptionMicroliters / 1_000_000;
        ecoFriendlyrouteDetail.setFuelConsumptionInLiters(fuelConsumptionLiters);
        
        ecoFriendlyrouteDetail.setDuration(ecoFriendlyRoute.getString("duration"));
        ecoFriendlyrouteDetail.setDistance(ecoFriendlyRoute.getDouble("distanceMeters") / 1000.0); // in km
        ecoFriendlyrouteDetail.setFuelType(fuelType);
        ecoFriendlyrouteDetail.setPolyline(ecoFriendlyRoute.getJSONObject("polyline").getString("encodedPolyline"));
        ecoFriendlyrouteDetail.setRouteToken(ecoFriendlyRoute.getString("routeToken"));
        ecoFriendlyrouteDetail.setRouteType("FUEL_EFFICIENT");

        Optional<ParkingLot> parkingLot = parkingLotRepository.findByPlaceId(destinationPlaceID);
        Integer occupancy = null;
        Integer capacity = null;
        if (parkingLot.isPresent()) {
            ParkingLot parkingLotEntity = parkingLot.get();
            occupancy = parkingLotEntity.getOccupancy();
            capacity = parkingLotEntity.getCapacity();
        }
        ecoFriendlyrouteDetail.setOccupancy(occupancy);
        ecoFriendlyrouteDetail.setCapacity(capacity);

        routeList.add(ecoFriendlyrouteDetail);

        if (!bothRoutesSame) {
            RouteDetailsModel defaultRouteDetailsModel = new RouteDetailsModel();
            travelAdvisory = defaultRoute.getJSONObject("travelAdvisory");
            fuelConsumptionMicroliters = travelAdvisory.getDouble("fuelConsumptionMicroliters");
            fuelConsumptionLiters = fuelConsumptionMicroliters / 1_000_000;
            defaultRouteDetailsModel.setFuelConsumptionInLiters(fuelConsumptionLiters);
            defaultRouteDetailsModel.setDuration(defaultRoute.getString("duration"));
            defaultRouteDetailsModel.setDistance(defaultRoute.getDouble("distanceMeters") / 1000.0); // in km
            defaultRouteDetailsModel.setFuelType(fuelType);
            defaultRouteDetailsModel.setPolyline(defaultRoute.getJSONObject("polyline").getString("encodedPolyline"));
            defaultRouteDetailsModel.setRouteToken(defaultRoute.getString("routeToken"));
            defaultRouteDetailsModel.setOccupancy(occupancy);
            defaultRouteDetailsModel.setCapacity(capacity);
            defaultRouteDetailsModel.setRouteType("DEFAULT_ROUTE");
            routeList.add(defaultRouteDetailsModel);
        }

        return routeList;
    }

    @Override
    public ParkingLot updateParkingLotFares(String fares) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ParkingLot> parkingLots = currentUser.getParkingLots();
        if (parkingLots == null || parkingLots.isEmpty()) {
            log.error("User with the id {} does not own a parking lot!", currentUser.getId());
            return null;
        }
        // assume every user can own at most one parking lot.
        ParkingLot parkingLot = parkingLots.get(0);
        parkingLot.setFares(fares);
        
        return parkingLotRepository.save(parkingLot);
    }

    @Override
    public ParkingLotDto getParkingLotById(Long id) {
        log.info("Parking Lot with the id: {} is requested", id);
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(id);

        if (!parkingLot.isPresent()) return null;

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
