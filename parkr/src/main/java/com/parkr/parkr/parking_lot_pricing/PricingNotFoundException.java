package com.parkr.parkr.parking_lot_pricing;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PricingNotFoundException extends RuntimeException{
    public PricingNotFoundException(String message){
        super(message);
    }
}
