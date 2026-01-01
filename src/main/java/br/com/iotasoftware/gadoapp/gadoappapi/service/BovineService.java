package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BovineDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.BovineRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.HerdRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BovineService {

    private final BovineRepository bovineRepository;
    private final HerdRepository herdRepository;

    public BovineService(BovineRepository bovineRepository, HerdRepository herdRepository) {
        this.bovineRepository = bovineRepository;
        this.herdRepository = herdRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void syncBovinesOverwriteSafely(List<BovineDTO> dtos) {
        bovineRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        for (BovineDTO dto : dtos) {
            Herd herd = null;
            if (dto.getHerdId() != null) {
                herd = herdRepository.findById(dto.getHerdId()).orElse(null);
            }

            Bovine newBovine = Bovine.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .status(dto.getStatus())
                    .gender(dto.getGender())
                    .breed(dto.getBreed())
                    .weight(dto.getWeight())
                    .birth(dto.getBirth())
                    .description(dto.getDescription())
                    .herd(herd)
                    .momId(dto.getMomId())
                    .dadId(dto.getDadId())
                    .build();
            bovineRepository.save(newBovine);
        }
    }
}
