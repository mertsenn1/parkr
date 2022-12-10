package com.parkr.parkr.lot;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotService implements ILotService
{
    private final LotCrawlerClient crawlerClient;

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
}
