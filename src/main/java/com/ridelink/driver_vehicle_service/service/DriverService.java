package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.*;
import com.ridelink.driver_vehicle_service.model.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.model.DriverStatus;

public interface DriverService {

    DriverResponse createDriver(CreateDriverRequest request);

    DriverResponse getDriverById(String driverId);

    DriverResponse getDriverByAccountId(String accountId);

    DriverResponse updateDriver(String driverId, UpdateDriverRequest request);

    DriverResponse updateDriverStatus(String driverId, DriverStatus status);

    DriverResponse updateAvailability(String driverId, AvailabilityStatus availabilityStatus);

    DriverResponse updateLocation(String driverId, Double latitude, Double longitude);

    DriverResponse updateServiceArea(String driverId, String serviceArea);
}
