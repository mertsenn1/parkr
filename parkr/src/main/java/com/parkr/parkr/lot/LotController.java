package com.parkr.parkr.lot;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("lots")
public class LotController
{
    private final ILotService lotService;
    @GetMapping
    ApiResponse getLocation(@RequestParam Double latitude, @RequestParam Double longitude,
                            @RequestParam(required=false) String language,
                            @RequestParam(required=false) Integer maxPrice,
                            @RequestParam(required=false) Integer minPrice,
                            @RequestParam(required=false) Boolean openNow,
                            @RequestParam(required=false) Integer radius,
                            @RequestParam(required=false) String type) {
        return ApiResponse.ok(lotService.getNearbyLots(latitude, longitude, language, maxPrice, minPrice,
                openNow, radius, type));
    }
}
