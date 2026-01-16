package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
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
public class HerdService {

    private final HerdRepository herdRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void syncHerds(List<HerdDTO> dtos) {
        User user = getAuthenticatedUser();
        for (HerdDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<Herd> existingOpt = herdRepository.findByIdAndUser(dto.getId(), user);
                if (existingOpt.isPresent()) {
                    Herd existing = existingOpt.get();
                    existing.setName(dto.getName());
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    herdRepository.save(existing);
                } else {
                    createOrUpdateByName(dto, user);
                }
            } else {
                createOrUpdateByName(dto, user);
            }
        }
    }
    
    // Adicionado 'synchronized' para evitar Race Condition na criação
    // Nota: Em um ambiente de cluster (vários servidores), isso não funcionaria.
    // Para cluster, precisaria de Unique Constraint no banco ou Lock Distribuído (Redis).
    private synchronized void createOrUpdateByName(HerdDTO dto, User user) {
        // Busca LISTA para evitar erro se houver duplicatas antigas
        List<Herd> duplicates = herdRepository.findByUserAndNameAndActiveTrue(user, dto.getName());
        
        if (!duplicates.isEmpty()) {
            // Já existe ativo! Atualiza o primeiro encontrado.
            Herd existing = duplicates.get(0);
            if (dto.getActive() != null) existing.setActive(dto.getActive());
            herdRepository.save(existing);
        } else {
            // Não existe ativo, cria novo
            Herd newHerd = Herd.builder()
                    .name(dto.getName())
                    .active(dto.getActive() != null ? dto.getActive() : true)
                    .user(user)
                    .build();
            herdRepository.save(newHerd);
        }
    }

    public List<HerdDTO> getAllHerds() {
        return herdRepository.findByUserAndActiveTrue(getAuthenticatedUser()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HerdDTO> getHerdsChangedSince(LocalDateTime since) {
        return herdRepository.findByUserAndUpdatedAtAfter(getAuthenticatedUser(), since).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<HerdDTO> getHerdById(Integer id) {
        return herdRepository.findByIdAndUser(id, getAuthenticatedUser())
                .filter(Herd::getActive)
                .map(this::convertToDTO);
    }

    public HerdDTO createHerd(HerdDTO dto) {
        // Verifica duplicidade antes de criar (para API direta)
        Optional<Herd> duplicate = herdRepository.findByUserAndNameAndActiveTrue(getAuthenticatedUser(), dto.getName())
                .stream().findFirst();
        
        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("Já existe um rebanho com este nome.");
        }

        Herd herd = Herd.builder()
                .name(dto.getName())
                .active(true)
                .user(getAuthenticatedUser())
                .build();
        Herd savedHerd = herdRepository.save(herd);
        return convertToDTO(savedHerd);
    }

    public Optional<HerdDTO> updateHerd(Integer id, HerdDTO dto) {
        return herdRepository.findByIdAndUser(id, getAuthenticatedUser())
                .filter(Herd::getActive)
                .map(existingHerd -> {
                    existingHerd.setName(dto.getName());
                    return convertToDTO(herdRepository.save(existingHerd));
                });
    }

    public boolean deleteHerd(Integer id) {
        return herdRepository.findByIdAndUser(id, getAuthenticatedUser())
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
