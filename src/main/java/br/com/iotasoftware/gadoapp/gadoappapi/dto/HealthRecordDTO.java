package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import br.com.iotasoftware.gadoapp.gadoappapi.model.HealthRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class HealthRecordDTO {

    private Integer id;

    @NotNull(message = "O ID do bovino é obrigatório")
    private Integer bovineId;

    @NotBlank(message = "O tipo é obrigatório")
    private String type;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String productName;

    @NotNull(message = "A data de aplicação é obrigatória")
    private Date appliedAt;

    private String dosage;
    private String veterinarian;
    private Date nextDueDate;
    private String notes;

    private Boolean active;
    private LocalDateTime updatedAt;

    private Integer tempId;

    public HealthRecordDTO(HealthRecord record) {
        this.id = record.getId();
        this.bovineId = record.getBovine() != null ? record.getBovine().getId() : null;
        this.type = record.getType();
        this.productName = record.getProductName();
        this.appliedAt = record.getAppliedAt();
        this.dosage = record.getDosage();
        this.veterinarian = record.getVeterinarian();
        this.nextDueDate = record.getNextDueDate();
        this.notes = record.getNotes();
        this.active = record.getActive();
        this.updatedAt = record.getUpdatedAt();
    }
}
