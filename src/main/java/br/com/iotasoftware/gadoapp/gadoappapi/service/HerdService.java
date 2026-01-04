package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.HerdRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HerdService {

    private final HerdRepository herdRepository;

    public HerdService(HerdRepository herdRepository) {
        this.herdRepository = herdRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void syncHerdsOverwriteSafely(List<HerdDTO> dtos) {
        herdRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        for (HerdDTO dto : dtos) {
            Herd newHerd = Herd.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .build();
            herdRepository.save(newHerd);
        }
    }

    public List<HerdDTO> getAllHerds() {
        return herdRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<HerdDTO> getHerdById(Integer id) {
        return herdRepository.findById(id)
                .map(this::convertToDTO);
    }

    public HerdDTO createHerd(HerdDTO dto) {
        // Se o ID for fornecido, ele será usado (útil para sincronização).
        // Se for nulo, o banco de dados gerará um novo ID (útil para criação via API).
        Herd herd = Herd.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
        Herd savedHerd = herdRepository.save(herd);
        return convertToDTO(savedHerd);
    }

    public Optional<HerdDTO> updateHerd(Integer id, HerdDTO dto) {
        return herdRepository.findById(id)
                .map(existingHerd -> {
                    existingHerd.setName(dto.getName());
                    return convertToDTO(herdRepository.save(existingHerd));
                });
    }

    public boolean deleteHerd(Integer id) {
        if (herdRepository.existsById(id)) {
            herdRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private HerdDTO convertToDTO(Herd herd) {
        return new HerdDTO(
                herd.getId(),
                herd.getName()
        );
    }
}
