package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    public String createCheckoutSession(User user, String priceId, String successUrl, String cancelUrl) {
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setClientReferenceId(String.valueOf(user.getId()))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPrice(priceId)
                                    .build()
                    )
                    .putMetadata("priceId", priceId);

            if (user.getStripeCustomerId() != null && !user.getStripeCustomerId().isEmpty()) {
                builder.setCustomer(user.getStripeCustomerId());
            } else {
                builder.setCustomerEmail(user.getEmail());
            }

            SessionCreateParams params = builder.build();
            Session session = Session.create(params);

            return session.getUrl();
        } catch (StripeException e) {
            log.error("Error creating Stripe checkout session", e);
            throw new RuntimeException("Error creating Stripe checkout session: " + e.getMessage());
        }
    }
}
