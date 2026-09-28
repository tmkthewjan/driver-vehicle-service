package com.ridelink.driver_vehicle_service.dto;

import com.ridelink.driver_vehicle_service.model.VehicleType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleDriverCriteria {

    @DecimalMin(value = "-90.0", message = "Pickup latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Pickup latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Pickup longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Pickup longitude must be between -180 and 180")
    private Double longitude;

    @Builder.Default
    @DecimalMin(value = "0.1", message = "Search radius must be greater than 0")
    private Double radiusKm = 10.0;

    private String serviceArea;

    private VehicleType vehicleType;

    @Min(value = 1, message = "Minimum capacity must be at least 1")
    private Integer minCapacity;
}
