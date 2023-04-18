package com.parkr.parkr.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class RequestBuilderCommon
{
    private static String GOOGLE_PLACES_API_KEY = "AIzaSyAgu7UnTtb-9hS2Aspkv6lp_n4Xu6Qm7ks";

    public static final String PLACES_REQUEST_URI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
            "location=%s,%s&language=%s&radius=5000&type=parking&key=%s";

    public static final String PLACE_REQUEST_URI = "https://maps.googleapis.com/maps/api/place/details/json?" +
            "place_id=%s&key=%s";

    public static RequestEntity<Void> buildRequestForLots(Double latitude, Double longitude, String language)
    {
        URI uri = encodeLotUrl(latitude, longitude, language);

        RequestEntity.HeadersBuilder<?> headersBuilder = RequestEntity.get(uri);

        return headersBuilder.build();
    }

    public static RequestEntity<Void> buildRequestForPlace(String placeID)
    {
        URI uri = encodePlaceUrl(placeID);

        RequestEntity.HeadersBuilder<?> headersBuilder = RequestEntity.get(uri);

        return headersBuilder.build();
    }

     
    private static URI encodeLotUrl(Double latitude, Double longitude, String language)
    {
        String url = String.format(PLACES_REQUEST_URI, latitude, longitude, language, GOOGLE_PLACES_API_KEY);

        String encodedUrl = UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .encode()
                .toUri()
                .toString();
        return UriComponentsBuilder.fromHttpUrl(encodedUrl)
                .build(true)
                .toUri();
    }

    private static URI encodePlaceUrl(String placeID)
    {
        String url = String.format(PLACE_REQUEST_URI, placeID, GOOGLE_PLACES_API_KEY);

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
