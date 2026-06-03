package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmMemberDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String role;
    private String status;
}
