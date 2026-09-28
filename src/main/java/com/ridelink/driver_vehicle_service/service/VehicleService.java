package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.RegisterVehicleRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateVehicleRequest;
import com.ridelink.driver_vehicle_service.dto.VehicleResponse;

public interface VehicleService {

    VehicleResponse registerVehicle(String driverId, RegisterVehicleRequest request);

    VehicleResponse getVehicleByDriverId(String driverId);

    VehicleResponse updateVehicle(String driverId, UpdateVehicleRequest request);

    VehicleResponse updateVehicleStatus(String driverId, boolean isActive);
}
