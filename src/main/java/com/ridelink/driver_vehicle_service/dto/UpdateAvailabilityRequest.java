package com.ridelink.driver_vehicle_service.dto;

import com.ridelink.driver_vehicle_service.model.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvailabilityRequest {

    @NotNull(message = "Availability status is required (AVAILABLE, UNAVAILABLE, ON_RIDE)")
    private AvailabilityStatus availabilityStatus;
}
