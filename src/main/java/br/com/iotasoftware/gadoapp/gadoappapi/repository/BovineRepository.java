package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BovineRepository extends JpaRepository<Bovine, Integer> {
    
    // Busca bovinos onde o rebanho pertence ao usuário X
    List<Bovine> findByHerdUserAndActiveTrue(User user);
    
    // Busca bovinos alterados onde o rebanho pertence ao usuário X
    List<Bovine> findByHerdUserAndUpdatedAtAfter(User user, LocalDateTime date);
    
    // Busca um bovino específico garantindo que ele pertence a um rebanho do usuário X
    Optional<Bovine> findByIdAndHerdUser(Integer id, User user);
    
    List<Bovine> findByHerdId(Integer herdId);
}
