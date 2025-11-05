package com.xyz.carrentalservice.repository;


import com.xyz.carrentalservice.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
