package com.parkr.parkr.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.parkr.parkr.car.FuelType;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RequestBuilderCommon
{
    private static String GOOGLE_PLACES_API_KEY = "AIzaSyAgu7UnTtb-9hS2Aspkv6lp_n4Xu6Qm7ks";

    public static final String PLACES_REQUEST_URI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
            "location=%s,%s&language=%s&radius=5000&type=parking&key=%s";

    public static final String PLACE_REQUEST_URI = "https://maps.googleapis.com/maps/api/place/details/json?" +
            "place_id=%s&key=%s";

        public static final String ROUTE_REQUEST_URI = "https://routes.googleapis.com/directions/v2:computeRoutes";

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

    public static RequestEntity<Map<String, Object>> buildRequestFuelEfficientRoute(Double originLatitude, Double originLongitude, String destinationPlaceID, String emissionType) throws IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Goog-Api-Key", GOOGLE_PLACES_API_KEY);
        headers.set("X-Goog-FieldMask", "routes.distanceMeters,routes.duration,routes.routeLabels,routes.routeToken,routes.travelAdvisory.fuelConsumptionMicroliters,routes.polyline");

        URI uri = UriComponentsBuilder.fromHttpUrl(ROUTE_REQUEST_URI).build().toUri();

        Map<String, Object> origin = new HashMap<>();
        origin.put("latitude", originLatitude);
        origin.put("longitude", originLongitude);

        Map<String, Object> vehicleInfo = new HashMap<>();
        vehicleInfo.put("emissionType", emissionType);

        Map<String, Object> routeModifiers = new HashMap<>();
        routeModifiers.put("vehicleInfo", vehicleInfo);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("origin", Map.of("location", Map.of("latLng", Map.of("latitude", originLatitude, "longitude", originLongitude))));
        requestBody.put("destination", Map.of("placeId", destinationPlaceID));
        requestBody.put("routeModifiers", Map.of("vehicleInfo", Map.of("emissionType", emissionType)));
        requestBody.put("travelMode", "DRIVE");
        requestBody.put("polylineQuality", "HIGH_QUALITY");
        requestBody.put("routingPreference", "TRAFFIC_AWARE_OPTIMAL");
        requestBody.put("extraComputations", List.of("FUEL_CONSUMPTION"));
        requestBody.put("requestedReferenceRoutes", List.of("FUEL_EFFICIENT"));

        return new RequestEntity<>(requestBody, headers, HttpMethod.POST, uri);
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
