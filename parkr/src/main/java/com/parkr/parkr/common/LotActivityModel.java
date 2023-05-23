package com.parkr.parkr.common;

import java.time.LocalDateTime;

import com.parkr.parkr.car.CarType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotActivityModel {
  private Long id;
  private String licensePlate;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer fee;
  private CarType carType;
  private String status;
}
