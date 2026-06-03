package br.com.iotasoftware.gadoapp.gadoappapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "farm_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "farm_id"})
})
public class FarmMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FarmRole role;

    @Column(nullable = false)
    private String status; // ACTIVE, PENDING

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @PrePersist
    protected void onCreate() {
        if (this.invitedAt == null) this.invitedAt = LocalDateTime.now();
        if (this.status == null) this.status = "ACTIVE";
    }
}
