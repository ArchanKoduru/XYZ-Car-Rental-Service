package com.xyz.carrentalservice.service;

import com.xyz.carrentalservice.config.CarPricingClient;
import com.xyz.carrentalservice.config.DrivingLicenseClient;
import com.xyz.carrentalservice.dto.BookingRequest;
import com.xyz.carrentalservice.dto.BookingResponse;
import com.xyz.carrentalservice.entities.Booking;
import com.xyz.carrentalservice.exception.*;
import com.xyz.carrentalservice.repository.BookingRepository;
import com.xyz.carrentalservice.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;

    private final DrivingLicenseClient licenseClient;
    private final CarPricingClient pricingClient;

    public Booking confirmBooking(BookingRequest request) {
        // Validate booking dates
        try {
            ValidationUtils.validateBookingDates(request.getStartDate(), request.getEndDate());
        } catch (IllegalArgumentException e) {
            throw new BookingValidationException(e.getMessage());
        }

        DrivingLicenseClient.LicenseResponse licenseResponse;
        try {
            licenseResponse = licenseClient.getLicenseDetails(
                    new DrivingLicenseClient.LicenseRequest(request.getDrivingLicenseNumber())
            );
        } catch (Exception e) {
            throw new ExternalServiceException("Driving license service unreachable");
        }

        try {
            ValidationUtils.validateLicense(licenseResponse, request.getAge());
        } catch (IllegalArgumentException e) {
            throw new LicenseValidationException(e.getMessage());
        }

        // Car pricing
        CarPricingClient.RateResponse rateResponse;
        try {
            rateResponse = pricingClient.getRate(
                    new CarPricingClient.RateRequest(request.getCarSegment().name())
            );
        } catch (Exception e) {
            throw new ExternalServiceException("Car pricing service unreachable");
        }

        try {
            ValidationUtils.validateRate(rateResponse);
        } catch (IllegalArgumentException e) {
            throw new RateUnavailableException(e.getMessage());
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        double rentalPrice = days * rateResponse.ratePerDay();

        Booking booking = Booking.builder()
                .drivingLicenseNumber(request.getDrivingLicenseNumber())
                .customerName(licenseResponse.ownerName())
                .age(request.getAge())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .carSegment(request.getCarSegment())
                .rentalPrice(rentalPrice)
                .build();

        return repository.save(booking);

    }

    public BookingResponse getBooking(Long id) {

                Booking response =  repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for id " + id));
        return BookingResponse.builder()
                .bookingId(response.getId())
                .drivingLicenseNumber(response.getDrivingLicenseNumber())
                .customerName(response.getCustomerName())
                .age(response.getAge())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .carSegment(response.getCarSegment())
                .rentalPrice(response.getRentalPrice())
                .build();
    }
}
