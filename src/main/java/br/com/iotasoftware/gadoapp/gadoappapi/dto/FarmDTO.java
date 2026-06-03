package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmDTO {
    private Integer id;
    private String name;
    private String inscricaoEstadual;
    private String city;
    private String state;
    private String address;
    private Double totalAreaHa;
    private String role; // user's role in this farm
    private Integer herdCount;
    private Integer bovineCount;
    private Integer memberCount;
}
