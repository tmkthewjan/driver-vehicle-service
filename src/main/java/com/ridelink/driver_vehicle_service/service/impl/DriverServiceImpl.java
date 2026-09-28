package com.ridelink.driver_vehicle_service.service.impl;

import com.ridelink.driver_vehicle_service.dto.*;
import com.ridelink.driver_vehicle_service.exception.InvalidOperationException;
import com.ridelink.driver_vehicle_service.exception.ResourceAlreadyExistsException;
import com.ridelink.driver_vehicle_service.exception.ResourceNotFoundException;
import com.ridelink.driver_vehicle_service.model.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.DriverStatus;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverServiceImpl.class);

    private final DriverRepository driverRepository;

    @Override
    public DriverResponse createDriver(CreateDriverRequest request) {
        log.info("Creating operational driver profile for accountId: {}", request.getAccountId());

        if (driverRepository.existsByAccountId(request.getAccountId())) {
            throw new ResourceAlreadyExistsException(
                    "Driver profile already exists for account ID: " + request.getAccountId());
        }

        Driver driver = Driver.builder()
                .accountId(request.getAccountId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .serviceArea(request.getServiceArea())
                .status(DriverStatus.INACTIVE)
                .availabilityStatus(AvailabilityStatus.UNAVAILABLE)
                .build();

        Driver saved = driverRepository.save(driver);
        log.info("Driver profile created with ID: {}", saved.getId());
        return DriverResponse.fromEntity(saved);
    }

    @Override
    public DriverResponse getDriverById(String driverId) {
        log.debug("Fetching driver by ID: {}", driverId);
        Driver driver = findDriverOrThrow(driverId);
        return DriverResponse.fromEntity(driver);
    }

    @Override
    public DriverResponse getDriverByAccountId(String accountId) {
        log.debug("Fetching driver by account ID: {}", accountId);
        Driver driver = driverRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver profile not found for account ID: " + accountId));
        return DriverResponse.fromEntity(driver);
    }

    @Override
    public DriverResponse updateDriver(String driverId, UpdateDriverRequest request) {
        log.info("Updating operational details for driver ID: {}", driverId);
        Driver driver = findDriverOrThrow(driverId);

        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setPhoneNumber(request.getPhoneNumber());
        if (request.getServiceArea() != null) {
            driver.setServiceArea(request.getServiceArea());
        }

        Driver updated = driverRepository.save(driver);
        return DriverResponse.fromEntity(updated);
    }

    @Override
    public DriverResponse updateDriverStatus(String driverId, DriverStatus newStatus) {
        log.info("Updating status for driver ID: {} to {}", driverId, newStatus);
        Driver driver = findDriverOrThrow(driverId);

        driver.setStatus(newStatus);

        // If driver becomes INACTIVE or SUSPENDED, they cannot remain AVAILABLE
        if ((newStatus == DriverStatus.INACTIVE || newStatus == DriverStatus.SUSPENDED)
                && driver.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE) {
            driver.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
            log.info("Driver ID: {} availability automatically set to UNAVAILABLE due to status {}",
                    driverId, newStatus);
        }

        Driver updated = driverRepository.save(driver);
        return DriverResponse.fromEntity(updated);
    }

    @Override
    public DriverResponse updateAvailability(String driverId, AvailabilityStatus newAvailability) {
        log.info("Updating availability for driver ID: {} to {}", driverId, newAvailability);
        Driver driver = findDriverOrThrow(driverId);

        // Validation rule: INACTIVE or SUSPENDED drivers cannot become AVAILABLE
        if (newAvailability == AvailabilityStatus.AVAILABLE && driver.getStatus() != DriverStatus.ACTIVE) {
            throw new InvalidOperationException(
                    "Driver cannot become AVAILABLE while status is " + driver.getStatus()
                            + ". Driver must be ACTIVE first.");
        }

        // Validation rule: Driver cannot transition directly to ON_RIDE if UNAVAILABLE or inactive
        if (newAvailability == AvailabilityStatus.ON_RIDE && driver.getStatus() != DriverStatus.ACTIVE) {
            throw new InvalidOperationException(
                    "Driver cannot transition to ON_RIDE while status is " + driver.getStatus());
        }

        driver.setAvailabilityStatus(newAvailability);
        Driver updated = driverRepository.save(driver);
        return DriverResponse.fromEntity(updated);
    }

    @Override
    public DriverResponse updateLocation(String driverId, Double latitude, Double longitude) {
        log.info("Updating simulated location for driver ID: {} to ({}, {})", driverId, latitude, longitude);
        Driver driver = findDriverOrThrow(driverId);

        if (latitude < -90.0 || latitude > 90.0) {
            throw new InvalidOperationException("Latitude must be between -90.0 and 90.0 degrees");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new InvalidOperationException("Longitude must be between -180.0 and 180.0 degrees");
        }

        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);

        Driver updated = driverRepository.save(driver);
        return DriverResponse.fromEntity(updated);
    }

    @Override
    public DriverResponse updateServiceArea(String driverId, String serviceArea) {
        log.info("Updating service area for driver ID: {} to {}", driverId, serviceArea);
        Driver driver = findDriverOrThrow(driverId);

        if (serviceArea == null || serviceArea.trim().isEmpty()) {
            throw new InvalidOperationException("Service area cannot be empty");
        }

        driver.setServiceArea(serviceArea.trim());
        Driver updated = driverRepository.save(driver);
        return DriverResponse.fromEntity(updated);
    }

    private Driver findDriverOrThrow(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
    }
}
