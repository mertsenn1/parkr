package com.parkr.parkr.lot;
import com.google.gson.JsonElement;


public interface ILotService
{
     JsonElement getNearbyLots(Double latitude, Double longitude, String language, Integer maxPrice,
                               Integer minPrice, Boolean openNow, Integer radius, String type);
}
