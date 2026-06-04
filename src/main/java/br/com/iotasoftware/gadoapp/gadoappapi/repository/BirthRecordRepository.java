package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.BirthRecord;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BirthRecordRepository extends JpaRepository<BirthRecord, Integer> {

    List<BirthRecord> findByMotherHerdUserAndActiveTrue(User user);

    List<BirthRecord> findByMotherHerdUserAndUpdatedAtAfter(User user, LocalDateTime date);

    Optional<BirthRecord> findByIdAndMotherHerdUser(Integer id, User user);

    List<BirthRecord> findByMotherIdAndActiveTrue(Integer motherId);

    // Farm-scoped queries
    List<BirthRecord> findByMotherHerdFarmIdAndActiveTrue(Integer farmId);

    List<BirthRecord> findByMotherHerdFarmIdAndUpdatedAtAfter(Integer farmId, LocalDateTime date);
}
