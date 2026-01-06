package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class BovineDTO {

    private Integer id;
    private String name;
    private String status;
    private String gender;
    private String breed;
    private Double weight;
    private Date birth;
    private String description;
    private Integer herdId;
    private Integer momId;
    private Integer dadId;
    private Boolean active;
    private LocalDateTime updatedAt;

    public BovineDTO(Bovine bovine) {
        this.id = bovine.getId();
        this.name = bovine.getName();
        this.status = bovine.getStatus();
        this.gender = bovine.getGender();
        this.breed = bovine.getBreed();
        this.weight = bovine.getWeight();
        this.birth = bovine.getBirth();
        this.description = bovine.getDescription();
        if (bovine.getHerd() != null) {
            this.herdId = bovine.getHerd().getId();
        }
        this.momId = bovine.getMomId();
        this.dadId = bovine.getDadId();
        this.active = bovine.getActive();
        this.updatedAt = bovine.getUpdatedAt();
    }
}
