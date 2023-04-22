package com.parkr.parkr.location;

import com.parkr.parkr.common.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("locations")
@SecurityRequirement(name = "parkr")
public class LocationController {
    private final ILocationService locationService;

    @GetMapping("{id}")
    public ApiResponse getLocationById(@PathVariable Long id) {
        return ApiResponse.ok(locationService.getLocationById(id));
    }

    @GetMapping
    public ApiResponse getAllLocations() {
        return ApiResponse.ok(locationService.getAllLocations());
    }

    @PostMapping()
    public ApiResponse saveLocation(@RequestBody LocationDto locationDto) {
        return ApiResponse.ok(locationService.saveLocation(locationDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
