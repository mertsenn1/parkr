package com.parkr.parkr.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingInfoModel {

  private Long id;
  private String name;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer fee;
  private Long carID;
  private Integer paidAmount;
  private LocalDateTime lastPaidTime;
  private String status;
}
