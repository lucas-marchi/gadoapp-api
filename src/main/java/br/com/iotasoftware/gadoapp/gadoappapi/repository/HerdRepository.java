package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HerdRepository extends JpaRepository<Herd, Integer> {
    List<Herd> findByUserAndActiveTrue(User user);
    List<Herd> findByUserAndUpdatedAtAfter(User user, LocalDateTime date);
    Optional<Herd> findByIdAndUser(Integer id, User user);
}
