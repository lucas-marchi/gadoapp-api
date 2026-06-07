package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.service.StripeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final StripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @AuthenticationPrincipal User user,
            @RequestBody CheckoutRequest request) {
        
        String url = stripeService.createCheckoutSession(
                user,
                request.getPriceId(),
                request.getSuccessUrl(),
                request.getCancelUrl()
        );

        return ResponseEntity.ok(Map.of("url", url));
    }

    @Data
    public static class CheckoutRequest {
        private String priceId;
        private String successUrl;
        private String cancelUrl;
    }
}
