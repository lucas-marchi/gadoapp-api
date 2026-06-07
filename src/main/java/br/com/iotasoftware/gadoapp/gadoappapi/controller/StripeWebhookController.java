package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.UserRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final UserRepository userRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            // Verify signature
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Error processing Stripe webhook payload", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload parsing error");
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            log.warn("Failed to deserialize Stripe webhook object.");
        }

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    if (stripeObject instanceof Session) {
                        Session session = (Session) stripeObject;
                        handleCheckoutSessionCompleted(session);
                    }
                    break;
                case "customer.subscription.deleted":
                case "invoice.payment_failed":
                    if (stripeObject instanceof com.stripe.model.Subscription) {
                        com.stripe.model.Subscription subscription = (com.stripe.model.Subscription) stripeObject;
                        handleSubscriptionFailedOrDeleted(subscription.getId());
                    } else if (stripeObject instanceof com.stripe.model.Invoice) {
                        com.stripe.model.Invoice invoice = (com.stripe.model.Invoice) stripeObject;
                        handleSubscriptionFailedOrDeleted(invoice.getSubscription());
                    }
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            log.error("Error handling Stripe event {}", event.getType(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    private void handleCheckoutSessionCompleted(Session session) {
        String clientReferenceId = session.getClientReferenceId();
        if (clientReferenceId == null) {
            log.warn("Checkout session completed without clientReferenceId. Session ID: {}", session.getId());
            return;
        }

        Integer userId;
        try {
            userId = Integer.parseInt(clientReferenceId);
        } catch (NumberFormatException e) {
            log.error("Invalid clientReferenceId (not an integer): {}", clientReferenceId);
            return;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStripeCustomerId(session.getCustomer());
            user.setStripeSubscriptionId(session.getSubscription());
            user.setSubscriptionStatus("active");
            if (session.getMetadata() != null && session.getMetadata().containsKey("priceId")) {
                user.setStripePriceId(session.getMetadata().get("priceId"));
            }
            userRepository.save(user);
            log.info("User {} subscription activated successfully.", userId);
        } else {
            log.warn("User with ID {} not found for checkout session {}", userId, session.getId());
        }
    }

    private void handleSubscriptionFailedOrDeleted(String subscriptionId) {
        if (subscriptionId == null) return;

        Optional<User> userOpt = userRepository.findByStripeSubscriptionId(subscriptionId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setSubscriptionStatus("canceled"); // Or past_due
            userRepository.save(user);
            log.info("User {} subscription canceled/failed.", user.getId());
        } else {
            log.warn("No user found with subscription ID {}", subscriptionId);
        }
    }
}
