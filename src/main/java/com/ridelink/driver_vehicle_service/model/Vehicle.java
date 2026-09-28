package com.ridelink.driver_vehicle_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents a vehicle registered to a driver in the RideLink system.
 * Each driver may have one vehicle at a time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;

    /** Reference to the owning driver. */
    @Indexed(unique = true)
    private String driverId;

    @Indexed(unique = true)
    private String registrationNumber;

    private String make;
    private String model;
    private VehicleType vehicleType;
    private String color;
    private int capacity;

    @Builder.Default
    private boolean isActive = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
