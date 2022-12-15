package com.parkr.parkr.parking_lot_pricing;

import com.parkr.parkr.car.CarType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingDto {
    private Long id;
    
    private String hourType;

    private double price;

    private String currency;

    private CarType carType;

    private Long parkingLotId;
}
