package com.ridelink.driver_vehicle_service.repository;

import com.ridelink.driver_vehicle_service.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for Vehicle documents.
 */
@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    Optional<Vehicle> findByDriverId(String driverId);

    boolean existsByDriverId(String driverId);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByDriverIdIn(List<String> driverIds);
}
