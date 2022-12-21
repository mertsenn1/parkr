package com.parkr.parkr.parking_lot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ParkingLotNotFoundException extends RuntimeException{
    public ParkingLotNotFoundException(String message){
        super(message);
    }
}
