package com.xyz.carrentalservice.dto;

import com.xyz.carrentalservice.entities.Car;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotBlank
    private String drivingLicenseNumber;

    @Min(18)
    private Integer age;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Car.CarSegment carSegment;
}
