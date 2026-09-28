package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.dto.RegisterVehicleRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateVehicleRequest;
import com.ridelink.driver_vehicle_service.dto.UpdateVehicleStatusRequest;
import com.ridelink.driver_vehicle_service.dto.VehicleResponse;
import com.ridelink.driver_vehicle_service.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers/{driverId}/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> registerVehicle(
            @PathVariable String driverId,
            @Valid @RequestBody RegisterVehicleRequest request) {
        VehicleResponse response = vehicleService.registerVehicle(driverId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable String driverId) {
        VehicleResponse response = vehicleService.getVehicleByDriverId(driverId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateVehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(driverId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status")
    public ResponseEntity<VehicleResponse> updateVehicleStatus(
            @PathVariable String driverId,
            @Valid @RequestBody UpdateVehicleStatusRequest request) {
        VehicleResponse response = vehicleService.updateVehicleStatus(driverId, request.getIsActive());
        return ResponseEntity.ok(response);
    }
}
