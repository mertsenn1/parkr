package com.parkr.parkr.parking_lot;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long>
{
    Optional<ParkingLot> findByPlaceId(String placeId);

    @Transactional
    @Modifying
    @Query(value = "update parking_lot set occupancy = occupancy + 1 where id = ?1", nativeQuery=true) // capacity check?
    void increaseParkingLotOccupancy(Long parkingLotID);

    @Transactional
    @Modifying
    @Query(value = "update parking_lot set occupancy = occupancy - 1 where id = ?1 AND occupancy > 0", nativeQuery = true)
    void decreaseParkingLotOccupancy(Long parkingLotID);
}
