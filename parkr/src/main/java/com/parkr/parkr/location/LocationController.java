package com.parkr.parkr.location;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("locations")
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
}
