package com.xyz.carrentalservice.config;

import com.xyz.carrentalservice.exception.LicenseValidationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("${api.license-url}")
public class LicenseStubController {

    @PostMapping
    public DrivingLicenseClient.LicenseResponse getLicense(@RequestBody DrivingLicenseClient.LicenseRequest request) {
        String dl = request.licenseNumber();

        // Expired license
        if ("DL_EXPIRED".equals(dl)) {
            return new DrivingLicenseClient.LicenseResponse("Jane Doe",
                    LocalDate.now().minusDays(1).toString());
        }

        // Less than 1 year valid license
        if ("DL_INVALID".equals(dl)) {
            return new DrivingLicenseClient.LicenseResponse("John Smith",
                    LocalDate.now().plusMonths(6).toString());
        }

        // Valid licenses
        if ("DL123".equals(dl)) {
            return new DrivingLicenseClient.LicenseResponse("John Doe",
                    LocalDate.now().plusYears(2).toString());
        }
        if ("DL456".equals(dl)) {
            return new DrivingLicenseClient.LicenseResponse("Alice Johnson",
                    LocalDate.now().plusYears(1).toString());
        }

        // Default: not found
        throw new LicenseValidationException( "Driving license not found");
    }
}
