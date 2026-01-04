package br.com.iotasoftware.gadoapp.gadoappapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bovines")
public class Bovine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String gender;

    private String breed;

    private Double weight;

    @Column(nullable = false)
    private Date birth;

    private String description;

    @ManyToOne
    @JoinColumn(name = "herd_id")
    @JsonBackReference
    private Herd herd;

    @Column(name = "mom_id")
    private Integer momId;

    @Column(name = "dad_id")
    private Integer dadId;

    public Bovine() {
    }
}
