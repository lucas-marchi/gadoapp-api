package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.model.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Integer> {

    List<WeightRecord> findByBovineHerdUserAndActiveTrue(User user);

    List<WeightRecord> findByBovineHerdUserAndUpdatedAtAfter(User user, LocalDateTime date);

    Optional<WeightRecord> findByIdAndBovineHerdUser(Integer id, User user);

    List<WeightRecord> findByBovineIdAndActiveTrue(Integer bovineId);

    // Farm-scoped queries
    List<WeightRecord> findByBovineHerdFarmIdAndActiveTrue(Integer farmId);

    List<WeightRecord> findByBovineHerdFarmIdAndUpdatedAtAfter(Integer farmId, LocalDateTime date);
}
