package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HerdRepository extends JpaRepository<Herd, Integer> {
    List<Herd> findByActiveTrue();
    List<Herd> findByUpdatedAtAfter(LocalDateTime date);
}
