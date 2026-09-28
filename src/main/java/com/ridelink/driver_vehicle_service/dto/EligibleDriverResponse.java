package com.ridelink.driver_vehicle_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleDriverResponse {

    private String driverId;
    private String accountId;
    private String fullName;
    private String phoneNumber;
    private String serviceArea;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double distanceKm;
    private VehicleResponse vehicle;
}
