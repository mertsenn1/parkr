package com.parkr.parkr.common;




import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public static JSONArray getPlacePhoto(String photoReference) {
        try
        {
            //RequestEntity<Void> requestEntity = RequestBuilderCommon.buildRequestForPlacePhoto(photoReference);
             
            RestTemplate restTemplate = new RestTemplate();
            /*
            HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.ALL));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange("https://maps.googleapis.com/maps/api/place/photo?max_width=400&photo_reference=AZose0m1i-AbtadICHa1lRjGWztAqSeXLqyrOrT1TyknIzLhlTgwy7RaDtq6aBH9glIc_9TjBamUteLugphFNOQy93-qn1Br3vbMfy-zdnUsRQOmsPqUQwKZ04VHM1YyiBxS5YKgdJcawyQIwlcM7Iv6RVSJcLd87SmnPK0619-8t-vdeOQ9&key=AIzaSyAgu7UnTtb-9hS2Aspkv6lp_n4Xu6Qm7ks",HttpMethod.GET,entity, byte[].class);
*/
            byte[] imageBytes = restTemplate.getForObject("https://maps.googleapis.com/maps/api/place/photo?max_width=400&photo_reference=AZose0m1i-AbtadICHa1lRjGWztAqSeXLqyrOrT1TyknIzLhlTgwy7RaDtq6aBH9glIc_9TjBamUteLugphFNOQy93-qn1Br3vbMfy-zdnUsRQOmsPqUQwKZ04VHM1YyiBxS5YKgdJcawyQIwlcM7Iv6RVSJcLd87SmnPK0619-8t-vdeOQ9&key=AIzaSyAgu7UnTtb-9hS2Aspkv6lp_n4Xu6Qm7ks", byte[].class);

            log.info("Finding place photo is successful for photo-reference: {}", photoReference);
            System.out.println(imageBytes);
            //JSONArray json = new JSONArray(response.getBody());
            return null;
        }
        catch (Exception ex) {
            log.error("Error occurred while finding place photo: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }


}
