package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Farm;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HerdRepository extends JpaRepository<Herd, Integer> {
    // Legacy user-based queries (kept for backwards compat during transition)
    List<Herd> findByUserAndActiveTrue(User user);
    List<Herd> findByUserAndUpdatedAtAfter(User user, LocalDateTime date);
    Optional<Herd> findByIdAndUser(Integer id, User user);
    List<Herd> findByUserAndNameAndActiveTrue(User user, String name);

    // Farm-scoped queries
    List<Herd> findByFarmAndActiveTrue(Farm farm);
    List<Herd> findByFarmAndUpdatedAtAfter(Farm farm, LocalDateTime date);
    Optional<Herd> findByIdAndFarm(Integer id, Farm farm);
    List<Herd> findByFarmAndNameAndActiveTrue(Farm farm, String name);
}
