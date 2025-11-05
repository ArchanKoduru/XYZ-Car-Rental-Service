package com.xyz.carrentalservice.config;

import com.xyz.carrentalservice.exception.RateUnavailableException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.rate-url}")
public class RateStubController {

    @PostMapping
    public CarPricingClient.RateResponse getRate(@RequestBody CarPricingClient.RateRequest request) {
        return switch (request.category().toUpperCase()) {
            case "SMALL" -> new CarPricingClient.RateResponse("SMALL", 20.65);
            case "MEDIUM" -> new CarPricingClient.RateResponse("MEDIUM", 40.88);
            case "LARGE" -> new CarPricingClient.RateResponse("LARGE", 70.65);
            case "EXTRA_LARGE" -> new CarPricingClient.RateResponse("EXTRA_LARGE", 98.95);
            default -> throw new RateUnavailableException( "Invalid car category");
        };
    }
}

