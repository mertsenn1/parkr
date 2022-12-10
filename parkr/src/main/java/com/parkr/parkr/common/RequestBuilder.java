package com.parkr.parkr.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class RequestBuilder
{
    @Value("${google.api.key}")
    private String GOOGLE_PLACES_API_KEY;

    public static final String REQUEST_URI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
            "location=%s,%s&language=%s&maxprice=%s&minprice=%s&opennow=%s&radius=%s&type=%s&key=%s";

    public RequestEntity<Void> buildRequestForLots(Double latitude, Double longitude, String language,
                                                   Integer maxPrice, Integer minPrice, Boolean openNow,
                                                   Integer radius, String type)
    {
        URI uri = encodeLotUrl(latitude, longitude, language, maxPrice, minPrice, openNow, radius, type);

        RequestEntity.HeadersBuilder<?> headersBuilder = RequestEntity.get(uri);

        return headersBuilder.build();
    }

    private URI encodeLotUrl(Double latitude, Double longitude, String language, Integer maxPrice,
                         Integer minPrice, Boolean openNow, Integer radius, String type)
    {
        String url = String.format(REQUEST_URI, latitude, longitude, language, maxPrice,
                                   minPrice, openNow, radius, type, GOOGLE_PLACES_API_KEY);

        String encodedUrl = UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .encode()
                .toUri()
                .toString();
        return UriComponentsBuilder.fromHttpUrl(encodedUrl)
                .build(true)
                .toUri();
    }
}
