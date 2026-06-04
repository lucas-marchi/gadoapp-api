package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.WeightRecordDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.model.WeightRecord;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.BovineRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.WeightRecordRepository;
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
public class WeightRecordService {

    private final WeightRecordRepository weightRecordRepository;
    private final BovineRepository bovineRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void syncWeightRecords(List<WeightRecordDTO> dtos) {
        User user = getAuthenticatedUser();

        for (WeightRecordDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<WeightRecord> existingOpt = weightRecordRepository.findByIdAndBovineHerdUser(dto.getId(), user);
                if (existingOpt.isPresent()) {
                    WeightRecord existing = existingOpt.get();
                    updateFromDTO(existing, dto, user);
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    weightRecordRepository.save(existing);
                } else {
                    createFromDTO(dto, user);
                }
            } else {
                createFromDTO(dto, user);
            }
        }
    }

    public List<WeightRecordDTO> getAllRecords() {
        return weightRecordRepository.findByBovineHerdUserAndActiveTrue(getAuthenticatedUser()).stream()
                .map(WeightRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<WeightRecordDTO> getAllRecords(Integer farmId) {
        return weightRecordRepository.findByBovineHerdFarmIdAndActiveTrue(farmId).stream()
                .map(WeightRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<WeightRecordDTO> getRecordsChangedSince(LocalDateTime since) {
        return weightRecordRepository.findByBovineHerdUserAndUpdatedAtAfter(getAuthenticatedUser(), since).stream()
                .map(WeightRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<WeightRecordDTO> getRecordsChangedSince(LocalDateTime since, Integer farmId) {
        return weightRecordRepository.findByBovineHerdFarmIdAndUpdatedAtAfter(farmId, since).stream()
                .map(WeightRecordDTO::new)
                .collect(Collectors.toList());
    }

    private WeightRecord createFromDTO(WeightRecordDTO dto, User user) {
        Bovine bovine = bovineRepository.findByIdAndHerdUser(dto.getBovineId(), user)
                .orElseThrow(() -> new RuntimeException("Bovino não encontrado ou não pertence ao usuário"));

        WeightRecord record = WeightRecord.builder()
                .bovine(bovine)
                .weight(dto.getWeight())
                .recordedAt(dto.getRecordedAt())
                .notes(dto.getNotes())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return weightRecordRepository.save(record);
    }

    private void updateFromDTO(WeightRecord record, WeightRecordDTO dto, User user) {
        if (dto.getBovineId() != null) {
            Bovine bovine = bovineRepository.findByIdAndHerdUser(dto.getBovineId(), user)
                    .orElseThrow(() -> new RuntimeException("Bovino não encontrado"));
            record.setBovine(bovine);
        }
        record.setWeight(dto.getWeight());
        record.setRecordedAt(dto.getRecordedAt());
        record.setNotes(dto.getNotes());
    }
}
