package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Integer> {
}
