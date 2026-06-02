package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import br.com.iotasoftware.gadoapp.gadoappapi.model.BirthRecord;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class BirthRecordDTO {

    private Integer id;

    @NotNull(message = "O ID da mãe é obrigatório")
    private Integer motherId;

    private Integer calfId;

    @NotNull(message = "A data de nascimento é obrigatória")
    private Date birthDate;

    private String notes;

    private Boolean active;
    private LocalDateTime updatedAt;

    private Integer tempId;

    public BirthRecordDTO(BirthRecord record) {
        this.id = record.getId();
        this.motherId = record.getMother() != null ? record.getMother().getId() : null;
        this.calfId = record.getCalfId();
        this.birthDate = record.getBirthDate();
        this.notes = record.getNotes();
        this.active = record.getActive();
        this.updatedAt = record.getUpdatedAt();
    }
}
