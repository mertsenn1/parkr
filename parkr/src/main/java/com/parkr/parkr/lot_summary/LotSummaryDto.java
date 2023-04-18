package com.parkr.parkr.lot_summary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.parkr.parkr.car.Car;
import com.parkr.parkr.parking_lot.ParkingLot;
import com.parkr.parkr.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LotSummaryDto {
    private Long id;

    private ParkingLot parkingLot;

    private Car car;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private int fee;
}
