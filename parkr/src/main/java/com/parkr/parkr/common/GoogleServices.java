package com.parkr.parkr.common;




import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.parkr.parkr.car.FuelType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleServices {

    public static JSONObject crawlNearbyLots(Double latitude, Double longitude, String language) {
        // Set default values if not specified
        if (language == null) {
            language = "en";
        }

        try
        {
            RequestEntity<Void> requestEntity = RequestBuilderCommon.buildRequestForLots(latitude, longitude, language);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            log.info("Lot crawling is successful for latitude: {} longitude: {}", latitude, longitude);

            JSONObject json = new JSONObject(response.getBody());
            return json;
        }
        catch (Exception ex) {
            log.error("Error occurred while crawling lots error: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static JSONObject getPlaceDetails(String placeID) {
        try
        {
            RequestEntity<Void> requestEntity = RequestBuilderCommon.buildRequestForPlace(placeID);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            log.info("Lot crawling is successful for placeID: {}", placeID);

            JSONObject json = new JSONObject(response.getBody());
            return json;
        }
        catch (Exception ex) {
            log.error("Error occurred while crawling lot error: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static JSONObject getEcoFriendlyRoute(Double originLatitude, Double originLongitude, String destinationPlaceID, String fuelType) {
        try
        {
            RequestEntity<Map<String, Object>> requestEntity = RequestBuilderCommon.buildRequestFuelEfficientRoute(originLatitude, originLongitude, destinationPlaceID, fuelType);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            log.info("Finding eco friendly route is successful for origin-latitude: {} origin_longitude: {} destination-parking-lot-placeID: {}", originLatitude, originLongitude, destinationPlaceID);

            JSONObject json = new JSONObject(response.getBody());
            return json;
        }
        catch (Exception ex) {
            log.error("Error occurred while finding fuel efficient routes: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static JSONArray getRouteDistances(Double originLatitude, Double originLongitude, List<String> destinationPlaceIDs) {
        try
        {
            RequestEntity<Map<String, Object>> requestEntity = RequestBuilderCommon.buildRequestForRouteDistances(originLatitude, originLongitude, destinationPlaceIDs);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            log.info("Finding route distances is successful for origin-latitude: {} origin_longitude: {}", originLatitude, originLongitude);

            JSONArray json = new JSONArray(response.getBody());
            return json;
        }
        catch (Exception ex) {
            log.error("Error occurred while finding route distances: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
