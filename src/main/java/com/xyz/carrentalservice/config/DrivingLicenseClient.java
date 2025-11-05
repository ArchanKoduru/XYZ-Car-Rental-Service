package com.xyz.carrentalservice.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "license-service", url = "${license.service.url}")
public interface DrivingLicenseClient {

    @PostMapping("${api.license-url}")
    LicenseResponse getLicenseDetails(@RequestBody LicenseRequest request);

    record LicenseRequest(String licenseNumber) {}
    record LicenseResponse(String ownerName, String expiryDate) {}
}