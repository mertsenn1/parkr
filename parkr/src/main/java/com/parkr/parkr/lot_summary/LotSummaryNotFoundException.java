package com.parkr.parkr.lot_summary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LotSummaryNotFoundException extends RuntimeException{
    public LotSummaryNotFoundException(String message){
        super(message);
    }
}
