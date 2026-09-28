package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.dto.CreateDriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.dto.UpdateAvailabilityRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateDriverRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateDriverStatusRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateLocationRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateServiceAreaRequest;
import com.ridelink.driver_vehicle_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable String driverId) {
        DriverResponse response = driverService.getDriverById(driverId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<DriverResponse> getDriverByAccountId(@PathVariable String accountId) {
        DriverResponse response = driverService.getDriverByAccountId(accountId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateDriverRequest request) {
        DriverResponse response = driverService.updateDriver(driverId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/status")
    public ResponseEntity<DriverResponse> updateDriverStatus(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateDriverStatusRequest request) {
        DriverResponse response = driverService.updateDriverStatus(driverId, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/availability")
    public ResponseEntity<DriverResponse> updateAvailability(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        DriverResponse response = driverService.updateAvailability(driverId, request.getAvailabilityStatus());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/location")
    public ResponseEntity<DriverResponse> updateLocation(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateLocationRequest request) {
        DriverResponse response = driverService.updateLocation(driverId, request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/service-area")
    public ResponseEntity<DriverResponse> updateServiceArea(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateServiceAreaRequest request) {
        DriverResponse response = driverService.updateServiceArea(driverId, request.getServiceArea());
        return ResponseEntity.ok(response);
    }
}
