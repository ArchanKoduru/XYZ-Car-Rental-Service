package com.xyz.carrentalservice.repository;


import com.xyz.carrentalservice.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findFirstBySegment(Car.CarSegment segment);
}
