package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionLimits {
    private int maxFarms;
    private int maxHerdsPerFarm;
    private int maxBovinesPerFarm;
    private int maxInvitesPerFarm;
}
