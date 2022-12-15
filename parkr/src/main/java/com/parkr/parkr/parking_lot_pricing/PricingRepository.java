package com.parkr.parkr.parking_lot_pricing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    
}
