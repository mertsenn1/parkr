package com.parkr.parkr.lot_summary;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LotSummaryRepository extends JpaRepository<LotSummary, Long> {
    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 AND L.end_time is null order by L.start_time desc", nativeQuery = true)
    List<LotSummary> getCurrentLotSummariesOfUser(Long id);

    @Query(value = "select L.*from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 AND L.end_time is not null order by L.end_time desc limit 10", nativeQuery = true)
    List<LotSummary> getPastLotSummariesOfUser(Long id);

    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 order by L.start_time desc limit 10", nativeQuery = true)
    List<LotSummary> getRecentLotSummaries(Long id);

    @Query(value = "select L.* from lot_summary L where L.car_id = ?1 AND L.end_time is null", nativeQuery = true)
    LotSummary getCurrentLotSummaryOfCar(Long id);

    @Query(value = "select L.* from lot_summary L where L.parking_lot_id = ?1 AND L.end_time is null order by L.start_time desc", nativeQuery = true)
    List<LotSummary> getCurrentLotSummariesOfParkingLot(Long id);

    @Query(value = "select L.* from lot_summary L where L.parking_lot_id = ?1 AND L.end_time is not null order by L.end_time desc", nativeQuery = true)
    List<LotSummary> getPastLotSummariesOfParkingLot(Long id);

    @Transactional
    @Modifying
    @Query(value = "update lot_summary set end_time = ?1, fee = ?2 where car_id = ?3 AND end_time is null", nativeQuery = true)
    void updateLotSummary(LocalDateTime endTime, Integer fee, Long carID);

    @Transactional
    @Modifying
    @Query(value = "update lot_summary set last_paid_time = ?1, paid_amount = ifnull(paid_amount,0) + ?2, status = ?3 where id = ?4", nativeQuery = true)
    void updateLotSummaryAfterPayment(LocalDateTime lastPaidTime, Integer paidAmount, String status, Long id);

    @Query(value = "select L.id from lot_summary L where L.car_id = ?1 AND L.parking_lot_id = ?2 AND L.end_time is null", nativeQuery = true)
    Long getExistingLotSummary(Long carID, Long parkingLotID);

}
