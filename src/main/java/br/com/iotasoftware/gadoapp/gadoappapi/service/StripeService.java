package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    private static final Set<String> ALLOWED_PRICE_IDS = Set.of(
            SubscriptionLimitService.PRICE_PRODUTOR,
            SubscriptionLimitService.PRICE_ENTERPRISE
    );

    public String createCheckoutSession(User user, String priceId, String successUrl, String cancelUrl) {
        validatePriceId(priceId);
        validateRedirectUrl(successUrl);
        validateRedirectUrl(cancelUrl);

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
            log.error("Error creating Stripe checkout session for user {}", user.getId(), e);
            throw new RuntimeException("Não foi possível iniciar o checkout. Tente novamente.");
        }
    }

    private void validatePriceId(String priceId) {
        if (priceId == null || !ALLOWED_PRICE_IDS.contains(priceId)) {
            throw new IllegalArgumentException("Plano de assinatura inválido.");
        }
    }

    private void validateRedirectUrl(String url) {
        try {
            URI uri = URI.create(url);
            String origin = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "");
            boolean valid = false;
            for (String allowed : allowedOrigins.split(",")) {
                if (origin.equals(allowed.trim())) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                throw new IllegalArgumentException("URL de redirecionamento não permitida.");
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("URL de redirecionamento não permitida.")) throw e;
            throw new IllegalArgumentException("URL de redirecionamento inválida.");
        }
    }
}
