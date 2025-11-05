package com.xyz.carrentalservice.util;

import com.xyz.carrentalservice.client.CarPricingClient;
import com.xyz.carrentalservice.client.DrivingLicenseClient;
import com.xyz.carrentalservice.exception.LicenseValidationException;
import com.xyz.carrentalservice.exception.RateUnavailableException;

import java.time.LocalDate;

public class ValidationUtils {

    public static void validateLicense(DrivingLicenseClient.LicenseResponse licenseResponse, int age) {
        if (licenseResponse == null
                || licenseResponse.ownerName() == null
                || licenseResponse.expiryDate() == null) {
            throw new LicenseValidationException("Driving license details not found.");
        }

        LocalDate expiryDate = LocalDate.parse(licenseResponse.expiryDate());
        if (expiryDate.isBefore(LocalDate.now().plusYears(1))) {
            throw new IllegalArgumentException("Driving license must be valid for at least 1 year.");
        }
    }
    public static void validateRate(CarPricingClient.RateResponse rateResponse) {

            if (rateResponse == null || rateResponse.ratePerDay() == null) {
            throw new RateUnavailableException("Rate not available for selected category");
        }

        try {
            ((Number) rateResponse.ratePerDay()).doubleValue();
        } catch (Exception e) {
            throw new RateUnavailableException("Invalid rate returned from pricing service");
        }
    }

    public static void validateBookingDates(LocalDate startDate, LocalDate endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            throw new IllegalArgumentException("Reservation end date must be after start date.");
        }
        if (days > 30) {
            throw new IllegalArgumentException("A car cannot be reserved for more than 30 days.");
        }
    }
}
