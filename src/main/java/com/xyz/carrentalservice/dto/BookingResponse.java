package com.xyz.carrentalservice.dto;


import com.xyz.carrentalservice.entities.Car;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BookingResponse {
    private Long bookingId;
    private String drivingLicenseNumber;
    private String customerName;
    private Integer age;
    private LocalDate startDate;
    private LocalDate endDate;
    private Car.CarSegment carSegment;
    private Double rentalPrice;
}
