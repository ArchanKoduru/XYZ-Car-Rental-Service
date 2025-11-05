package com.xyz.carrentalservice.controller;

import com.xyz.carrentalservice.client.CarPricingClient;
import com.xyz.carrentalservice.client.DrivingLicenseClient;
import com.xyz.carrentalservice.dto.BookingRequest;
import com.xyz.carrentalservice.entities.Car;
import com.xyz.carrentalservice.repository.CarRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private DrivingLicenseClient licenseClient;

    @MockBean
    private CarPricingClient pricingClient;

    @Autowired
    private CarRepository carRepository;


    private BookingRequest validRequest;
    private BookingRequest expiredLicenseRequest;
    private BookingRequest tooLongBookingRequest;

    @BeforeEach
    void setUp() {
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
        expiredLicenseRequest.setEndDate(LocalDate.now().plusDays(3));

        tooLongBookingRequest = new BookingRequest();
        tooLongBookingRequest.setDrivingLicenseNumber("DL123");
        tooLongBookingRequest.setAge(25);
        tooLongBookingRequest.setCarSegment(Car.CarSegment.MEDIUM);
        tooLongBookingRequest.setStartDate(LocalDate.now());
        tooLongBookingRequest.setEndDate(LocalDate.now().plusDays(31));

        // Mock DrivingLicenseClient
        Mockito.when(licenseClient.getLicenseDetails(any()))
                .thenAnswer(inv -> {
                    DrivingLicenseClient.LicenseRequest req = inv.getArgument(0);
                    return switch(req.licenseNumber()) {
                        case "DL123" -> new DrivingLicenseClient.LicenseResponse("John Doe", LocalDate.now().plusYears(2).toString());
                        case "DL_EXPIRED" -> new DrivingLicenseClient.LicenseResponse("Jane Doe", LocalDate.now().minusDays(1).toString());
                        case "DL_LESS_1Y" -> new DrivingLicenseClient.LicenseResponse("Baby Driver", LocalDate.now().minusMonths(6).toString());
                        default -> throw new RuntimeException("Driving license service unreachable");
                    };
                });

        // Mock CarPricingClient
        Mockito.when(pricingClient.getRate(any()))
                .thenAnswer(inv -> {
                    CarPricingClient.RateRequest req = inv.getArgument(0);
                    double rate = switch (req.category()) {
                        case "SMALL" -> 20.65;
                        case "MEDIUM" -> 40.88;
                        case "LARGE" -> 70.65;
                        case "EXTRA_LARGE" -> 98.95;
                        default -> 0.0;
                    };
                    return new CarPricingClient.RateResponse(req.category(), rate);
                });
    }

    @Test
    void shouldHaveCarsInH2() {
        assertEquals(4, carRepository.count());
    }

    @Test
    void shouldReturnSuccessForValidBooking() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/bookings", validRequest, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("bookingId"));
    }

    @Test
    void shouldReturnErrorForExpiredLicense() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/bookings", expiredLicenseRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Driving license must be valid"));
    }

    @Test
    void shouldReturnErrorForBookingLongerThan30Days() {
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1//bookings", tooLongBookingRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("A car cannot be reserved for more than 30 days"));
    }
}
