package com.parkr.parkr.lot_summary;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LotSummaryRepository extends JpaRepository<LotSummary, Long> {
    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 AND L.end_time is null", nativeQuery = true)
    List<LotSummary> getCurrentLotSummariesOfUser(Long id);

    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 AND L.end_time is not null", nativeQuery = true)
    List<LotSummary> getPastLotSummariesOfUser(Long id);

    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 limit 10", nativeQuery = true)
    List<LotSummary> getRecentLotSummaries(Long id);

    @Transactional
    @Modifying
    @Query(value = "update lot_summary set end_time = ?1 where car_id = ?2 AND end_time is null", nativeQuery = true)
    void updateEndTime(LocalDateTime endTime, Long carID);

    @Query(value = "select L.id from lot_summary L where L.car_id = ?1 AND L.parking_lot_id = ?2 AND L.end_time is null", nativeQuery = true)
    Long getExistingLotSummary(Long carID, Long parkingLotID);

}
