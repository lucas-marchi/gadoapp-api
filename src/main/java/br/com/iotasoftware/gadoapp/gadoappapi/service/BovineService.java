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
import java.util.Optional;
import java.util.stream.Collectors;

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
            saveBovineFromDTO(dto);
        }
    }

    public List<BovineDTO> getAllBovines() {
        return bovineRepository.findAll().stream()
                .map(BovineDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<BovineDTO> getBovineById(Integer id) {
        return bovineRepository.findById(id)
                .map(BovineDTO::new);
    }

    public BovineDTO createBovine(BovineDTO dto) {
        Bovine savedBovine = saveBovineFromDTO(dto);
        return new BovineDTO(savedBovine);
    }

    public Optional<BovineDTO> updateBovine(Integer id, BovineDTO dto) {
        return bovineRepository.findById(id)
                .map(existingBovine -> {
                    updateBovineFromDTO(existingBovine, dto);
                    return new BovineDTO(bovineRepository.save(existingBovine));
                });
    }

    public boolean deleteBovine(Integer id) {
        if (bovineRepository.existsById(id)) {
            bovineRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Bovine saveBovineFromDTO(BovineDTO dto) {
        Herd herd = null;
        if (dto.getHerdId() != null) {
            herd = herdRepository.findById(dto.getHerdId()).orElse(null);
        }

        // Se o ID for fornecido, ele será usado (útil para sincronização).
        // Se for nulo, o banco de dados gerará um novo ID (útil para criação via API).
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
        return bovineRepository.save(newBovine);
    }

    private void updateBovineFromDTO(Bovine bovine, BovineDTO dto) {
        bovine.setName(dto.getName());
        bovine.setStatus(dto.getStatus());
        bovine.setGender(dto.getGender());
        bovine.setBreed(dto.getBreed());
        bovine.setWeight(dto.getWeight());
        bovine.setBirth(dto.getBirth());
        bovine.setDescription(dto.getDescription());
        bovine.setMomId(dto.getMomId());
        bovine.setDadId(dto.getDadId());

        if (dto.getHerdId() != null) {
            Herd herd = herdRepository.findById(dto.getHerdId()).orElse(null);
            bovine.setHerd(herd);
        } else {
            bovine.setHerd(null);
        }
    }
}
