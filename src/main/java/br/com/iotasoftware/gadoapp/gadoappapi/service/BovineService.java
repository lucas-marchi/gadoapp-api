package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BovineDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.BovineRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.HerdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BovineService {

    private final BovineRepository bovineRepository;
    private final HerdRepository herdRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void syncBovines(List<BovineDTO> dtos) {
        User user = getAuthenticatedUser();
        for (BovineDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<Bovine> existingOpt = bovineRepository.findByIdAndHerdUser(dto.getId(), user);
                if (existingOpt.isPresent()) {
                    Bovine existing = existingOpt.get();
                    updateBovineFromDTO(existing, dto, user);
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    bovineRepository.save(existing);
                } else {
                    saveBovineFromDTO(dto, user);
                }
            } else {
                saveBovineFromDTO(dto, user);
            }
        }
    }

    public List<BovineDTO> getAllBovines() {
        return bovineRepository.findByHerdUserAndActiveTrue(getAuthenticatedUser()).stream()
                .map(BovineDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<BovineDTO> getBovinesChangedSince(LocalDateTime since) {
        return bovineRepository.findByHerdUserAndUpdatedAtAfter(getAuthenticatedUser(), since).stream()
                .map(BovineDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<BovineDTO> getBovineById(Integer id) {
        return bovineRepository.findByIdAndHerdUser(id, getAuthenticatedUser())
                .filter(Bovine::getActive)
                .map(BovineDTO::new);
    }

    public BovineDTO createBovine(BovineDTO dto) {
        dto.setId(null); // Garante criação
        dto.setActive(true);
        Bovine savedBovine = saveBovineFromDTO(dto, getAuthenticatedUser());
        return new BovineDTO(savedBovine);
    }

    public Optional<BovineDTO> updateBovine(Integer id, BovineDTO dto) {
        return bovineRepository.findByIdAndHerdUser(id, getAuthenticatedUser())
                .filter(Bovine::getActive)
                .map(existingBovine -> {
                    updateBovineFromDTO(existingBovine, dto, getAuthenticatedUser());
                    return new BovineDTO(bovineRepository.save(existingBovine));
                });
    }

    public boolean deleteBovine(Integer id) {
        return bovineRepository.findByIdAndHerdUser(id, getAuthenticatedUser())
                .map(bovine -> {
                    bovine.setActive(false);
                    bovineRepository.save(bovine);
                    return true;
                }).orElse(false);
    }

    private Bovine saveBovineFromDTO(BovineDTO dto, User user) {
        Herd herd = null;
        if (dto.getHerdId() != null) {
            // Verifica se o rebanho pertence ao usuário logado!
            herd = herdRepository.findByIdAndUser(dto.getHerdId(), user)
                    .orElseThrow(() -> new RuntimeException("Rebanho não encontrado ou não pertence ao usuário"));
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
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return bovineRepository.save(newBovine);
    }

    private void updateBovineFromDTO(Bovine bovine, BovineDTO dto, User user) {
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
            // Verifica se o novo rebanho pertence ao usuário logado
            Herd herd = herdRepository.findByIdAndUser(dto.getHerdId(), user)
                    .orElseThrow(() -> new RuntimeException("Rebanho não encontrado ou não pertence ao usuário"));
            bovine.setHerd(herd);
        } else {
            bovine.setHerd(null);
        }
    }
}
