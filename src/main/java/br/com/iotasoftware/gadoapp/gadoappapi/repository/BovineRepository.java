package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BovineRepository extends JpaRepository<Bovine, Integer> {
    List<Bovine> findByHerdId(Integer herdId);
    List<Bovine> findByActiveTrue();
    List<Bovine> findByUpdatedAtAfter(LocalDateTime date);
}
