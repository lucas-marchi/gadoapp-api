package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Farm;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.FarmMemberRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.FarmRepository;
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
    private final FarmRepository farmRepository;
    private final FarmMemberRepository farmMemberRepository;
    private final br.com.iotasoftware.gadoapp.gadoappapi.service.SubscriptionLimitService limitService;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // ===== FARM-SCOPED METHODS =====

    @Transactional
    public void syncHerds(List<HerdDTO> dtos, Integer farmId) {
        User user = getAuthenticatedUser();
        assertFarmMembership(user, farmId);
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

        for (HerdDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<Herd> existingOpt = herdRepository.findByIdAndFarm(dto.getId(), farm);
                if (existingOpt.isPresent()) {
                    Herd existing = existingOpt.get();
                    existing.setName(dto.getName());
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    herdRepository.save(existing);
                } else {
                    createOrUpdateByNameForFarm(dto, user, farm);
                }
            } else {
                createOrUpdateByNameForFarm(dto, user, farm);
            }
        }
    }

    // Legacy user-scoped sync
    @Transactional
    public void syncHerds(List<HerdDTO> dtos) {
        User user = getAuthenticatedUser();
        for (HerdDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<Herd> existingOpt = herdRepository.findByIdAndUser(dto.getId(), user);
                if (existingOpt.isPresent()) {
                    Herd existing = existingOpt.get();
                    existing.setName(dto.getName());
                    if (dto.getActive() != null)
                        existing.setActive(dto.getActive());
                    herdRepository.save(existing);
                } else {
                    createOrUpdateByName(dto, user);
                }
            } else {
                createOrUpdateByName(dto, user);
            }
        }
    }

    private synchronized void createOrUpdateByNameForFarm(HerdDTO dto, User user, Farm farm) {
        List<Herd> duplicates = herdRepository.findByFarmAndNameAndActiveTrue(farm, dto.getName());

        if (!duplicates.isEmpty()) {
            Herd existing = duplicates.get(0);
            if (dto.getActive() != null) existing.setActive(dto.getActive());
            herdRepository.save(existing);
        } else {
            var limits = limitService.getLimitsForUser(user);
            long currentHerds = herdRepository.findByFarmAndActiveTrue(farm).size();
            if (currentHerds >= limits.getMaxHerdsPerFarm()) {
                throw new br.com.iotasoftware.gadoapp.gadoappapi.exception.LimitExceededException("Limite de rebanhos atingido para esta propriedade no plano atual.");
            }
            Herd newHerd = Herd.builder()
                    .name(dto.getName())
                    .active(dto.getActive() != null ? dto.getActive() : true)
                    .user(user)
                    .farm(farm)
                    .build();
            herdRepository.save(newHerd);
        }
    }

    private synchronized void createOrUpdateByName(HerdDTO dto, User user) {
        List<Herd> duplicates = herdRepository.findByUserAndNameAndActiveTrue(user, dto.getName());

        if (!duplicates.isEmpty()) {
            Herd existing = duplicates.get(0);
            if (dto.getActive() != null)
                existing.setActive(dto.getActive());
            herdRepository.save(existing);
        } else {
            // Note: in legacy mode, we don't have farm context easily here, but we can restrict total herds globally or assume single farm.
            // For now, limiting globally against maxHerdsPerFarm since free has 1 farm anyway.
            var limits = limitService.getLimitsForUser(user);
            long currentHerds = herdRepository.findByUserAndActiveTrue(user).size();
            if (currentHerds >= limits.getMaxHerdsPerFarm()) {
                throw new br.com.iotasoftware.gadoapp.gadoappapi.exception.LimitExceededException("Limite de rebanhos atingido no plano atual.");
            }

            Herd newHerd = Herd.builder()
                    .name(dto.getName())
                    .active(dto.getActive() != null ? dto.getActive() : true)
                    .user(user)
                    .build();
            herdRepository.save(newHerd);
        }
    }

    // Farm-scoped queries
    public List<HerdDTO> getAllHerds(Integer farmId) {
        assertFarmMembership(getAuthenticatedUser(), farmId);
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));
        return herdRepository.findByFarmAndActiveTrue(farm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HerdDTO> getHerdsChangedSince(LocalDateTime since, Integer farmId) {
        assertFarmMembership(getAuthenticatedUser(), farmId);
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));
        return herdRepository.findByFarmAndUpdatedAtAfter(farm, since).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void assertFarmMembership(User user, Integer farmId) {
        var membership = farmMemberRepository.findByUserIdAndFarmId(user.getId(), farmId)
                .orElseThrow(() -> new SecurityException("Access denied: not a member of this farm"));
        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new SecurityException("Access denied: membership not active");
        }
    }

    // Legacy user-scoped queries
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
        User user = getAuthenticatedUser();
        Optional<Herd> duplicate = herdRepository.findByUserAndNameAndActiveTrue(user, dto.getName())
                .stream().findFirst();

        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("Já existe um rebanho com este nome.");
        }

        var limits = limitService.getLimitsForUser(user);
        long currentHerds = herdRepository.findByUserAndActiveTrue(user).size();
        if (currentHerds >= limits.getMaxHerdsPerFarm()) {
            throw new br.com.iotasoftware.gadoapp.gadoappapi.exception.LimitExceededException("Limite de rebanhos atingido no plano atual.");
        }

        Herd herd = Herd.builder()
                .name(dto.getName())
                .active(true)
                .user(user)
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
                herd.getUpdatedAt());
    }
}
