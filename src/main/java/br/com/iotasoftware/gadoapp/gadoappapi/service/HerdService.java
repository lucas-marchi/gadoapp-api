package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.HerdRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public void syncHerds(List<HerdDTO> dtos) {
        for (HerdDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<Herd> existingOpt = herdRepository.findById(dto.getId());
                if (existingOpt.isPresent()) {
                    Herd existing = existingOpt.get();
                    // Lógica de conflito simples: última escrita vence (baseado no updatedAt do servidor vs cliente se necessário)
                    // Aqui assumimos que o cliente manda a verdade
                    existing.setName(dto.getName());
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    herdRepository.save(existing);
                } else {
                    // ID existe no cliente mas não no servidor? Criar novo com esse ID
                    createHerdFromSync(dto);
                }
            } else {
                createHerdFromSync(dto);
            }
        }
    }
    
    private void createHerdFromSync(HerdDTO dto) {
        Herd newHerd = Herd.builder()
                .id(dto.getId())
                .name(dto.getName())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        herdRepository.save(newHerd);
    }

    public List<HerdDTO> getAllHerds() {
        return herdRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HerdDTO> getHerdsChangedSince(LocalDateTime since) {
        return herdRepository.findByUpdatedAtAfter(since).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<HerdDTO> getHerdById(Integer id) {
        return herdRepository.findById(id)
                .filter(Herd::getActive)
                .map(this::convertToDTO);
    }

    public HerdDTO createHerd(HerdDTO dto) {
        Herd herd = Herd.builder()
                .name(dto.getName())
                .active(true)
                .build();
        Herd savedHerd = herdRepository.save(herd);
        return convertToDTO(savedHerd);
    }

    public Optional<HerdDTO> updateHerd(Integer id, HerdDTO dto) {
        return herdRepository.findById(id)
                .filter(Herd::getActive)
                .map(existingHerd -> {
                    existingHerd.setName(dto.getName());
                    return convertToDTO(herdRepository.save(existingHerd));
                });
    }

    public boolean deleteHerd(Integer id) {
        return herdRepository.findById(id)
                .map(herd -> {
                    herd.setActive(false);
                    herdRepository.save(herd);
                    return true;
                }).orElse(false);
    }

    private HerdDTO convertToDTO(Herd herd) {
        return new HerdDTO(
                herd.getId(),
                herd.getName(),
                herd.getActive(),
                herd.getUpdatedAt()
        );
    }
}
