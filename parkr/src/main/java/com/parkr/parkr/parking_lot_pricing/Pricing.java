package com.parkr.parkr.parking_lot_pricing;

import com.parkr.parkr.car.CarType;
import com.parkr.parkr.parking_lot.ParkingLot;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hour_type")
    private String hourType;

    @Column(name = "price")
    private double price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "car_type")
    @Enumerated(EnumType.STRING)
    private CarType carType;

    @ManyToOne
    @JoinColumn(name = "parking_lot_id", referencedColumnName = "id")
    private ParkingLot parkingLot;

}
