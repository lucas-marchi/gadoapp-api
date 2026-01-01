package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Date getBirth() { return birth; }
    public void setBirth(Date birth) { this.birth = birth; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getHerdId() { return herdId; }
    public void setHerdId(Integer herdId) { this.herdId = herdId; }

    public Integer getMomId() { return momId; }
    public void setMomId(Integer momId) { this.momId = momId; }

    public Integer getDadId() { return dadId; }
    public void setDadId(Integer dadId) { this.dadId = dadId; }
}
