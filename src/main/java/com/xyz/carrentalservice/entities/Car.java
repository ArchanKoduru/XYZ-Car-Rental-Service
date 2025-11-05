package com.xyz.carrentalservice.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    /**
     * One Car can have multiple bookings over time.
     * The 'mappedBy' value refers to the 'car' field in Booking.
     */
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    public enum CarSegment {
        SMALL, MEDIUM, LARGE, EXTRA_LARGE
    }
}

