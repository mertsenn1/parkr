package com.parkr.parkr.parking_lot_pricing;

import java.util.List;

public interface IPricingService {
    PricingDto getPricingById(Long id);

    List<PricingDto> getAllPricings();

    Pricing savePricing(PricingDto pricingDto, Long parkingLotId);
}
