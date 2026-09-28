package com.ridelink.driver_vehicle_service.service.impl;

import com.ridelink.driver_vehicle_service.dto.EligibleDriverCriteria;
import com.ridelink.driver_vehicle_service.dto.EligibleDriverResponse;
import com.ridelink.driver_vehicle_service.dto.VehicleResponse;
import com.ridelink.driver_vehicle_service.model.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.DriverStatus;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.repository.VehicleRepository;
import com.ridelink.driver_vehicle_service.service.EligibleDriverService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EligibleDriverServiceImpl implements EligibleDriverService {

    private static final Logger log = LoggerFactory.getLogger(EligibleDriverServiceImpl.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public List<EligibleDriverResponse> findEligibleDrivers(EligibleDriverCriteria criteria) {
        log.info("Searching for eligible drivers with criteria: {}", criteria);

        // 1. Retrieve all drivers who are ACTIVE and AVAILABLE
        List<Driver> activeAvailableDrivers;
        if (criteria.getServiceArea() != null && !criteria.getServiceArea().trim().isEmpty()) {
            activeAvailableDrivers = driverRepository.findByStatusAndAvailabilityStatusAndServiceArea(
                    DriverStatus.ACTIVE,
                    AvailabilityStatus.AVAILABLE,
                    criteria.getServiceArea().trim());
        } else {
            activeAvailableDrivers = driverRepository.findByStatusAndAvailabilityStatus(
                    DriverStatus.ACTIVE,
                    AvailabilityStatus.AVAILABLE);
        }

        if (activeAvailableDrivers.isEmpty()) {
            log.info("No active and available drivers found matching basic status/service area");
            return Collections.emptyList();
        }

        // 2. Fetch vehicles for these drivers in batch to avoid N+1 queries
        List<String> driverIds = activeAvailableDrivers.stream()
                .map(Driver::getId)
                .collect(Collectors.toList());

        List<Vehicle> vehicles = vehicleRepository.findByDriverIdIn(driverIds);
        Map<String, Vehicle> vehicleByDriverId = vehicles.stream()
                .collect(Collectors.toMap(Vehicle::getDriverId, v -> v, (v1, v2) -> v1));

        // 3. Filter candidates by active vehicle, vehicle type, capacity, and location radius
        boolean hasCoordinates = criteria.getLatitude() != null && criteria.getLongitude() != null;
        double radius = (criteria.getRadiusKm() != null && criteria.getRadiusKm() > 0)
                ? criteria.getRadiusKm() : 10.0;

        List<EligibleDriverResponse> eligibleList = new ArrayList<>();

        for (Driver driver : activeAvailableDrivers) {
            Vehicle vehicle = vehicleByDriverId.get(driver.getId());

            // Rule: Driver must have a registered vehicle
            if (vehicle == null) {
                continue;
            }

            // Rule: Driver's vehicle must be active
            if (!vehicle.isActive()) {
                continue;
            }

            // Rule: Vehicle type filtering (if requested)
            if (criteria.getVehicleType() != null && vehicle.getVehicleType() != criteria.getVehicleType()) {
                continue;
            }

            // Rule: Capacity requirement (if requested)
            if (criteria.getMinCapacity() != null && vehicle.getCapacity() < criteria.getMinCapacity()) {
                continue;
            }

            // Rule: Location radius check (if pickup coordinates provided)
            Double distanceKm = null;
            if (hasCoordinates) {
                if (driver.getCurrentLatitude() == null || driver.getCurrentLongitude() == null) {
                    // Driver has no simulated coordinates, exclude from geo search
                    continue;
                }

                double distance = calculateDistanceKm(
                        criteria.getLatitude(), criteria.getLongitude(),
                        driver.getCurrentLatitude(), driver.getCurrentLongitude());

                if (distance > radius) {
                    // Outside search radius
                    continue;
                }
                distanceKm = distance;
            }

            String fullName = ((driver.getFirstName() != null ? driver.getFirstName() : "") + " "
                    + (driver.getLastName() != null ? driver.getLastName() : "")).trim();

            eligibleList.add(EligibleDriverResponse.builder()
                    .driverId(driver.getId())
                    .accountId(driver.getAccountId())
                    .fullName(fullName)
                    .phoneNumber(driver.getPhoneNumber())
                    .serviceArea(driver.getServiceArea())
                    .currentLatitude(driver.getCurrentLatitude())
                    .currentLongitude(driver.getCurrentLongitude())
                    .distanceKm(distanceKm)
                    .vehicle(VehicleResponse.fromEntity(vehicle))
                    .build());
        }

        // 4. Sort by distance ascending (closest driver first) if coordinates were provided
        if (hasCoordinates) {
            eligibleList.sort(Comparator.comparing(
                    EligibleDriverResponse::getDistanceKm,
                    Comparator.nullsLast(Double::compareTo)));
        }

        log.info("Found {} eligible driver(s) matching criteria", eligibleList.size());
        return eligibleList;
    }

    /**
     * Calculates the great-circle distance between two points on the Earth
     * using the Haversine formula, rounded to 2 decimal places.
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;
        return Math.round(distance * 100.0) / 100.0;
    }
}
