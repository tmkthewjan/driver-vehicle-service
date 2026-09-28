package com.ridelink.driver_vehicle_service.repository;

import com.ridelink.driver_vehicle_service.model.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.DriverStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for Driver documents.
 */
@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    Optional<Driver> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);

    List<Driver> findByStatusAndAvailabilityStatus(DriverStatus status, AvailabilityStatus availabilityStatus);

    List<Driver> findByStatusAndAvailabilityStatusAndServiceArea(
            DriverStatus status, AvailabilityStatus availabilityStatus, String serviceArea);
}
