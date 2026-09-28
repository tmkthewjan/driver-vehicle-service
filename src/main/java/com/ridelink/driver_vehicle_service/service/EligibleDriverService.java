package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.EligibleDriverCriteria;
import com.ridelink.driver_vehicle_service.dto.EligibleDriverResponse;

import java.util.List;

public interface EligibleDriverService {

    List<EligibleDriverResponse> findEligibleDrivers(EligibleDriverCriteria criteria);
}
