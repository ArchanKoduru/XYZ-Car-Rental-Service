package com.xyz.carrentalservice.service;

import com.xyz.carrentalservice.client.CarPricingClient;
import com.xyz.carrentalservice.client.DrivingLicenseClient;
import com.xyz.carrentalservice.dto.BookingRequest;
import com.xyz.carrentalservice.dto.BookingResponse;
import com.xyz.carrentalservice.entities.Booking;
import com.xyz.carrentalservice.entities.Car;
import com.xyz.carrentalservice.exception.*;
import com.xyz.carrentalservice.repository.BookingRepository;
import com.xyz.carrentalservice.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository repository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private DrivingLicenseClient licenseClient;

    @Mock
    private CarPricingClient pricingClient;

    private BookingRequest validRequest;
    private BookingRequest expiredLicenseRequest;
    private BookingRequest tooLongBookingRequest;

    @BeforeEach
    void setup() {

        validRequest = new BookingRequest();
        validRequest.setDrivingLicenseNumber("DL123");
        validRequest.setAge(25);
        validRequest.setCarSegment(Car.CarSegment.MEDIUM);
        validRequest.setStartDate(LocalDate.now());
        validRequest.setEndDate(LocalDate.now().plusDays(5));

        expiredLicenseRequest = new BookingRequest();
        expiredLicenseRequest.setDrivingLicenseNumber("DL_EXPIRED");
        expiredLicenseRequest.setAge(25);
        expiredLicenseRequest.setCarSegment(Car.CarSegment.SMALL);
        expiredLicenseRequest.setStartDate(LocalDate.now());
        expiredLicenseRequest.setEndDate(LocalDate.now().plusDays(5));

        tooLongBookingRequest = new BookingRequest();
        tooLongBookingRequest.setDrivingLicenseNumber("DL123");
        tooLongBookingRequest.setAge(25);
        tooLongBookingRequest.setCarSegment(Car.CarSegment.MEDIUM);
        tooLongBookingRequest.setStartDate(LocalDate.now());
        tooLongBookingRequest.setEndDate(LocalDate.now().plusDays(31));


    }

    @Test
    void confirmBooking_successful() {
        // Mock DrivingLicenseClient
        when(licenseClient.getLicenseDetails(any()))
                .thenReturn(new DrivingLicenseClient.LicenseResponse("John Doe", LocalDate.now().plusYears(2).toString()));

        // Mock CarPricingClient
        when(pricingClient.getRate(any()))
                .thenReturn(new CarPricingClient.RateResponse("MEDIUM", 50.0));
        Car car = Car.builder()
                .id(1L)
                .carName("Toyota Corolla")
                .segment(Car.CarSegment.MEDIUM)
                .dailyRate(50.0)
                .build();
        when(carRepository.findFirstBySegment(Car.CarSegment.MEDIUM))
                .thenReturn(Optional.of(car));

        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking booking = bookingService.confirmBooking(validRequest);

        assertNotNull(booking);
        verify(repository).save(any());
    }

    @Test
    void confirmBooking_expiredLicense_throws() {
        when(licenseClient.getLicenseDetails(any()))
                .thenReturn(new DrivingLicenseClient.LicenseResponse("Jane Doe", LocalDate.now().minusDays(1).toString()));

        LicenseValidationException ex = assertThrows(
                LicenseValidationException.class,
                () -> bookingService.confirmBooking(expiredLicenseRequest)
        );

        assertTrue(ex.getMessage().contains("valid"));
        verify(repository, never()).save(any());
    }

    @Test
    void confirmBooking_tooLongBooking_throws() {
        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> bookingService.confirmBooking(tooLongBookingRequest)
        );

        assertTrue(ex.getMessage().contains("more than 30 days"));
        verify(repository, never()).save(any());
    }

    @Test
    void confirmBooking_licenseServiceUnavailable_throws() {
        when(licenseClient.getLicenseDetails(any()))
                .thenThrow(new RuntimeException("Service down"));

        ExternalServiceException ex = assertThrows(
                ExternalServiceException.class,
                () -> bookingService.confirmBooking(validRequest)
        );

        assertTrue(ex.getMessage().contains("Driving license service unreachable"));
        verify(repository, never()).save(any());
    }

    @Test
    void confirmBooking_rateServiceUnavailable_throws() {
        when(licenseClient.getLicenseDetails(any()))
                .thenReturn(new DrivingLicenseClient.LicenseResponse("John Doe", LocalDate.now().plusYears(2).toString()));

        when(pricingClient.getRate(any()))
                .thenThrow(new RuntimeException("Service down"));

        ExternalServiceException ex = assertThrows(
                ExternalServiceException.class,
                () -> bookingService.confirmBooking(validRequest)
        );

        assertTrue(ex.getMessage().contains("Car pricing service unreachable"));
        verify(repository, never()).save(any());
    }

    @Test
    void getBooking_found() {
        Booking booking = Booking.builder()
                .id(1L)
                .drivingLicenseNumber("DL123")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(booking));

        BookingResponse result = bookingService.getBooking(1L);

        assertNotNull(result);
        assertEquals("DL123", result.getDrivingLicenseNumber());
    }

    @Test
    void getBooking_notFound_throws() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        BookingNotFoundException ex = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBooking(1L)
        );

        assertTrue(ex.getMessage().contains("Booking not found"));
    }
}
