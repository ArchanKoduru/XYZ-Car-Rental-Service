package com.xyz.carrentalservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String drivingLicenseNumber;
    private String customerName;
    private Integer age;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Car.CarSegment carSegment;

    private Double rentalPrice;

    // Relationship to Car
    @OneToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
