package com.ridelink.driver_vehicle_service.dto;

import com.ridelink.driver_vehicle_service.model.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private String id;
    private String accountId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private DriverStatus status;
    private AvailabilityStatus availabilityStatus;
    private String serviceArea;
    private Double currentLatitude;
    private Double currentLongitude;
    private Instant createdAt;
    private Instant updatedAt;

    public static DriverResponse fromEntity(Driver driver) {
        if (driver == null) {
            return null;
        }
        return DriverResponse.builder()
                .id(driver.getId())
                .accountId(driver.getAccountId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .phoneNumber(driver.getPhoneNumber())
                .status(driver.getStatus())
                .availabilityStatus(driver.getAvailabilityStatus())
                .serviceArea(driver.getServiceArea())
                .currentLatitude(driver.getCurrentLatitude())
                .currentLongitude(driver.getCurrentLongitude())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
