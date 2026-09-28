package com.ridelink.driver_vehicle_service.service.impl;

import com.ridelink.driver_vehicle_service.dto.RegisterVehicleRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateVehicleRequest;
import com.ridelink.driver_vehicle_service.dto.VehicleResponse;
import com.ridelink.driver_vehicle_service.exception.ResourceAlreadyExistsException;
import com.ridelink.driver_vehicle_service.exception.ResourceNotFoundException;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.repository.VehicleRepository;
import com.ridelink.driver_vehicle_service.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    @Override
    public VehicleResponse registerVehicle(String driverId, RegisterVehicleRequest request) {
        log.info("Registering vehicle for driver ID: {}, regNumber: {}", driverId, request.getRegistrationNumber());

        ensureDriverExists(driverId);

        if (vehicleRepository.existsByDriverId(driverId)) {
            throw new ResourceAlreadyExistsException(
                    "Driver with ID " + driverId + " already has a registered vehicle");
        }

        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new ResourceAlreadyExistsException(
                    "Vehicle with registration number " + request.getRegistrationNumber() + " already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .driverId(driverId)
                .registrationNumber(request.getRegistrationNumber().trim().toUpperCase())
                .make(request.getMake().trim())
                .model(request.getModel().trim())
                .vehicleType(request.getVehicleType())
                .color(request.getColor().trim())
                .capacity(request.getCapacity())
                .isActive(true)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle registered successfully with ID: {} for driver ID: {}", saved.getId(), driverId);
        return VehicleResponse.fromEntity(saved);
    }

    @Override
    public VehicleResponse getVehicleByDriverId(String driverId) {
        log.debug("Fetching vehicle for driver ID: {}", driverId);
        ensureDriverExists(driverId);

        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No vehicle found for driver ID: " + driverId));
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    public VehicleResponse updateVehicle(String driverId, UpdateVehicleRequest request) {
        log.info("Updating vehicle details for driver ID: {}", driverId);
        ensureDriverExists(driverId);

        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No vehicle found for driver ID: " + driverId));

        vehicle.setMake(request.getMake().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor().trim());
        vehicle.setCapacity(request.getCapacity());

        Vehicle updated = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(updated);
    }

    @Override
    public VehicleResponse updateVehicleStatus(String driverId, boolean isActive) {
        log.info("Updating vehicle status for driver ID: {} to isActive={}", driverId, isActive);
        ensureDriverExists(driverId);

        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No vehicle found for driver ID: " + driverId));

        vehicle.setActive(isActive);
        Vehicle updated = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(updated);
    }

    private void ensureDriverExists(String driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver not found with ID: " + driverId);
        }
    }
}
