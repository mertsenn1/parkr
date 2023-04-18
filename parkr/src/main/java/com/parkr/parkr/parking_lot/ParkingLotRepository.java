package com.parkr.parkr.parking_lot;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long>
{
    Optional<ParkingLot> findByPlaceId(String placeId);
}
