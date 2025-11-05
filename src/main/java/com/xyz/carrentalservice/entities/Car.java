package com.xyz.carrentalservice.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carName;

    @Enumerated(EnumType.STRING)
    private CarSegment segment;



    private Double dailyRate;

    // Link to Booking if booked
    @OneToOne(mappedBy = "car")
    private Booking booking;

    public enum CarSegment {
        SMALL, MEDIUM, LARGE, EXTRA_LARGE
    }
}

