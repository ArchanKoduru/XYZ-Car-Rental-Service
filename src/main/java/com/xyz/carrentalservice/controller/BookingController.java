package com.xyz.carrentalservice.controller;

import com.xyz.carrentalservice.dto.BookingRequest;
import com.xyz.carrentalservice.dto.BookingResponse;
import com.xyz.carrentalservice.entities.Booking;
import com.xyz.carrentalservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> confirmBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.confirmBooking(request);
        return ResponseEntity.ok(Map.of("bookingId", booking.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse>  getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }
}
