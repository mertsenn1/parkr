package com.parkr.parkr.common;




import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;

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
}
