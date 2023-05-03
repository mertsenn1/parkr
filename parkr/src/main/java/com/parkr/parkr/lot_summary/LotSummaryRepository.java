package com.parkr.parkr.lot_summary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LotSummaryRepository extends JpaRepository<LotSummary, Long> {
    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 AND L.end_time is null", nativeQuery = true)
    List<LotSummary> getCurrentLotSummariesOfUser(Long id);

    @Query(value = "select L.* from lot_summary L join car C ON L.car_id = C.id where C.user_id = ?1 AND L.end_time is not null", nativeQuery = true)
    List<LotSummary> getPastLotSummariesOfUser(Long id);


}
