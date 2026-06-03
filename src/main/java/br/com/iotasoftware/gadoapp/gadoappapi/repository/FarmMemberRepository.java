package br.com.iotasoftware.gadoapp.gadoappapi.repository;

import br.com.iotasoftware.gadoapp.gadoappapi.model.FarmMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmMemberRepository extends JpaRepository<FarmMember, Integer> {

    List<FarmMember> findByUserIdAndStatus(Integer userId, String status);

    List<FarmMember> findByUserIdAndStatusNot(Integer userId, String status);

    List<FarmMember> findByFarmIdAndStatus(Integer farmId, String status);

    Optional<FarmMember> findByUserIdAndFarmId(Integer userId, Integer farmId);

    List<FarmMember> findByUserEmail(String email);

    List<FarmMember> findByUserEmailAndStatus(String email, String status);
}
