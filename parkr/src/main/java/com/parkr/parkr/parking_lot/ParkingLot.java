package com.parkr.parkr.parking_lot;

import com.parkr.parkr.address.Address;
import com.parkr.parkr.location.Location;
import com.parkr.parkr.user.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="parking_lot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLot
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "place_id")
    private String placeId;

    @Column(name = "fares")
    private String fares;

    @Column(name = "photo_url")
    private String photoUrl;

    // ToDo: Opening Hours will be added later.

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ParkingLotStatus status;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "occupancy")
    private int occupancy;
    /*
     * 	id        +++++ 
	⁃	name      +++++
	⁃	location_id (fk) +++++
	⁃	address_id (fk) +++++
	⁃	owner_id (fk) +++++
	⁃	review_id (fk) NOOO IT WILL HAVE MANY REVIEWS
	⁃	price_level ++++++
	⁃	openning_hours
	⁃	photo_url++++++
	⁃	status(enum)+++++++
	⁃	capacity++++++++
	⁃	occupancy++++++++
     */

    
}
