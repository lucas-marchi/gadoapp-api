package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class BovineDTO {

    private Integer id;

    @NotBlank(message = "O nome/brinco é obrigatório")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    private String name;

    @NotBlank(message = "O status é obrigatório")
    private String status;

    @NotBlank(message = "O gênero é obrigatório")
    private String gender;

    @Size(max = 50)
    private String breed;

    private Double weight;

    @NotNull(message = "A data de nascimento é obrigatória")
    @PastOrPresent(message = "A data de nascimento não pode ser no futuro")
    private Date birth;

    @Size(max = 500)
    private String description;

    private Integer herdId;
    private Integer momId;
    private Integer dadId;
    private Boolean active;
    private LocalDateTime updatedAt;

    private Integer tempId;
    private Integer momTempId;
    private Integer dadTempId;

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
