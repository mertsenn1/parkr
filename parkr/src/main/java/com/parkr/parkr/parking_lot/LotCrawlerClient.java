package com.parkr.parkr.parking_lot;

import com.google.gson.JsonElement;
import com.parkr.parkr.common.RequestBuilderCommon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
@RequiredArgsConstructor
public class LotCrawlerClient
{
    private final RequestBuilderCommon requestBuilderCommon;

    public JsonElement crawlNearbyLots(Double latitude, Double longitude, String language, Integer maxPrice,
                                       Integer minPrice, Boolean openNow, Integer radius, String type) {
        RequestEntity<Void> requestEntity = requestBuilderCommon.buildRequestForLots(latitude, longitude, language, maxPrice, minPrice, openNow, radius, type);

        try
        {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<JsonElement> results = restTemplate.exchange(requestEntity, JsonElement.class);


            log.info("Lot crawling is successful for latitude: {} longitude: {} radius: {}", latitude, longitude, radius);

            return results.getBody();
        }
        catch (Exception ex) {
            log.error("Error occurred while crawling lots error: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
