package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.SubscriptionLimits;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionLimitService {

    // IDs vindos do ambiente (Stripe)
    public static final String PRICE_PRODUTOR = "price_1TfTGmKHCIRT9fmkCYZ9DwXd";
    public static final String PRICE_ENTERPRISE = "price_1TfTHWKHCIRT9fmkxY5wbWeD";

    public SubscriptionLimits getLimitsForUser(User user) {
        String status = user.getSubscriptionStatus();
        String priceId = user.getStripePriceId();

        if ("active".equals(status) && priceId != null) {
            if (priceId.equals(PRICE_PRODUTOR)) {
                // Plano Produtor: 2 Fazendas, 10 Rebanhos cada, Bovinos ilimitados, 3 convites
                return SubscriptionLimits.builder()
                        .maxFarms(2)
                        .maxHerdsPerFarm(10)
                        .maxBovinesPerFarm(Integer.MAX_VALUE) // Ilimitado
                        .maxInvitesPerFarm(3)
                        .build();
            } else if (priceId.equals(PRICE_ENTERPRISE)) {
                // Plano Enterprise: Tudo ilimitado
                return SubscriptionLimits.builder()
                        .maxFarms(Integer.MAX_VALUE)
                        .maxHerdsPerFarm(Integer.MAX_VALUE)
                        .maxBovinesPerFarm(Integer.MAX_VALUE)
                        .maxInvitesPerFarm(Integer.MAX_VALUE)
                        .build();
            }
        }

        // Default / Plano Free: 1 Fazenda, 1 Rebanho, 20 Bovinos, 0 convites
        return SubscriptionLimits.builder()
                .maxFarms(1)
                .maxHerdsPerFarm(1)
                .maxBovinesPerFarm(20)
                .maxInvitesPerFarm(0)
                .build();
    }
}
