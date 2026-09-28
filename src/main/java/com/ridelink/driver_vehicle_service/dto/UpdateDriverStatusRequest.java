package com.ridelink.driver_vehicle_service.dto;

import com.ridelink.driver_vehicle_service.model.DriverStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusRequest {

    @NotNull(message = "Driver status is required (ACTIVE, INACTIVE, SUSPENDED)")
    private DriverStatus status;
}
