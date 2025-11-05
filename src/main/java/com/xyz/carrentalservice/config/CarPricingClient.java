package com.xyz.carrentalservice.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rate-service", url = "${rate.service.url}")
public interface CarPricingClient {

    @PostMapping("${api.rate-url}")
    RateResponse getRate(@RequestBody RateRequest request);

    record RateRequest(String category) {}
    record RateResponse(String category, Double ratePerDay) {}
}
