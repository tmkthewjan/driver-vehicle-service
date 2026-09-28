package com.ridelink.driver_vehicle_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleStatusRequest {

    @NotNull(message = "Active status is required")
    private Boolean isActive;
}
