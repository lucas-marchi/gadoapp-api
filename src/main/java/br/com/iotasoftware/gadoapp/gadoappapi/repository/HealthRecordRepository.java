package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.HealthRecord;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Integer> {

    List<HealthRecord> findByBovineHerdUserAndActiveTrue(User user);

    List<HealthRecord> findByBovineHerdUserAndUpdatedAtAfter(User user, LocalDateTime date);

    Optional<HealthRecord> findByIdAndBovineHerdUser(Integer id, User user);

    List<HealthRecord> findByBovineIdAndActiveTrue(Integer bovineId);

    // Farm-scoped queries
    List<HealthRecord> findByBovineHerdFarmIdAndActiveTrue(Integer farmId);

    List<HealthRecord> findByBovineHerdFarmIdAndUpdatedAtAfter(Integer farmId, LocalDateTime date);
}
