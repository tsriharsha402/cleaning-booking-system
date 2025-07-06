package com.justlife.cleaning.cleaning_service.repository;

import com.justlife.cleaning.cleaning_service.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
