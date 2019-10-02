package com.udacity.pricing.api;



import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.service.PriceException;
import com.udacity.pricing.service.PricingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implements a REST-based controller for the pricing service.
 */
@RestController
@RequestMapping("#H2\n" +
        "spring.h2.console.path=/h2\n" +
        "spring.datasource.url=jdbc:h2:mem:data\n" +
        "\n" +
        "#Eureka Client\n" +
        "spring.application.name=pricing-service\n" +
        "server.port=8082\n" +
        "eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/\n" +
        "eureka.client.service-url.default-zone=http://localhost:8761/eureka/\n" +
        "eureka.instance.prefer-ip-address=false")
public class PricingController {

    /**
     * Gets the price for a requested vehicle.
     * @param vehicleId ID number of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */
    @GetMapping
    public Price get(@RequestParam Long vehicleId) {
        try {
            return PricingService.getPrice(vehicleId);
        } catch (PriceException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Price Not Found", ex);
        }

    }
}
