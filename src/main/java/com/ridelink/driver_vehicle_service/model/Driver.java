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
 * Represents a driver's operational profile in the RideLink system.
 * The driver's authentication account is managed by the Account Service;
 * this document only stores the operational/driving-related data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers")
public class Driver {

    @Id
    private String id;

    /** Reference to the user account in the Account Service. */
    @Indexed(unique = true)
    private String accountId;

    private String firstName;
    private String lastName;
    private String phoneNumber;

    @Builder.Default
    private DriverStatus status = DriverStatus.INACTIVE;

    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNAVAILABLE;

    private String serviceArea;

    private Double currentLatitude;
    private Double currentLongitude;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
