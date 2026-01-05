package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class HerdDTO {
    private Integer id;
    private String name;
    private Boolean active;
    private LocalDateTime updatedAt;

    public HerdDTO(Integer id, String name, Boolean active, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
