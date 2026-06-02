package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import br.com.iotasoftware.gadoapp.gadoappapi.model.WeightRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class WeightRecordDTO {

    private Integer id;

    @NotNull(message = "O ID do bovino é obrigatório")
    private Integer bovineId;

    @NotNull(message = "O peso é obrigatório")
    @Positive(message = "O peso deve ser positivo")
    private Double weight;

    @NotNull(message = "A data de pesagem é obrigatória")
    private Date recordedAt;

    private String notes;

    private Boolean active;
    private LocalDateTime updatedAt;

    private Integer tempId;

    public WeightRecordDTO(WeightRecord record) {
        this.id = record.getId();
        this.bovineId = record.getBovine() != null ? record.getBovine().getId() : null;
        this.weight = record.getWeight();
        this.recordedAt = record.getRecordedAt();
        this.notes = record.getNotes();
        this.active = record.getActive();
        this.updatedAt = record.getUpdatedAt();
    }
}
