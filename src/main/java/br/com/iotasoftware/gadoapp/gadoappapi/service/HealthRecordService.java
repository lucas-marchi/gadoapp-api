package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HealthRecordDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import br.com.iotasoftware.gadoapp.gadoappapi.model.HealthRecord;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.BovineRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.HealthRecordRepository;
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
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final BovineRepository bovineRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void syncHealthRecords(List<HealthRecordDTO> dtos) {
        User user = getAuthenticatedUser();

        for (HealthRecordDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<HealthRecord> existingOpt = healthRecordRepository.findByIdAndBovineHerdUser(dto.getId(), user);
                if (existingOpt.isPresent()) {
                    HealthRecord existing = existingOpt.get();
                    updateFromDTO(existing, dto, user);
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    healthRecordRepository.save(existing);
                } else {
                    createFromDTO(dto, user);
                }
            } else {
                createFromDTO(dto, user);
            }
        }
    }

    public List<HealthRecordDTO> getAllRecords() {
        return healthRecordRepository.findByBovineHerdUserAndActiveTrue(getAuthenticatedUser()).stream()
                .map(HealthRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<HealthRecordDTO> getAllRecords(Integer farmId) {
        return healthRecordRepository.findByBovineHerdFarmIdAndActiveTrue(farmId).stream()
                .map(HealthRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<HealthRecordDTO> getRecordsChangedSince(LocalDateTime since) {
        return healthRecordRepository.findByBovineHerdUserAndUpdatedAtAfter(getAuthenticatedUser(), since).stream()
                .map(HealthRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<HealthRecordDTO> getRecordsChangedSince(LocalDateTime since, Integer farmId) {
        return healthRecordRepository.findByBovineHerdFarmIdAndUpdatedAtAfter(farmId, since).stream()
                .map(HealthRecordDTO::new)
                .collect(Collectors.toList());
    }

    private HealthRecord createFromDTO(HealthRecordDTO dto, User user) {
        Bovine bovine = bovineRepository.findByIdAndHerdUser(dto.getBovineId(), user)
                .orElseThrow(() -> new RuntimeException("Bovino não encontrado ou não pertence ao usuário"));

        HealthRecord record = HealthRecord.builder()
                .bovine(bovine)
                .type(dto.getType())
                .productName(dto.getProductName())
                .appliedAt(dto.getAppliedAt())
                .dosage(dto.getDosage())
                .veterinarian(dto.getVeterinarian())
                .nextDueDate(dto.getNextDueDate())
                .notes(dto.getNotes())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return healthRecordRepository.save(record);
    }

    private void updateFromDTO(HealthRecord record, HealthRecordDTO dto, User user) {
        if (dto.getBovineId() != null) {
            Bovine bovine = bovineRepository.findByIdAndHerdUser(dto.getBovineId(), user)
                    .orElseThrow(() -> new RuntimeException("Bovino não encontrado"));
            record.setBovine(bovine);
        }
        record.setType(dto.getType());
        record.setProductName(dto.getProductName());
        record.setAppliedAt(dto.getAppliedAt());
        record.setDosage(dto.getDosage());
        record.setVeterinarian(dto.getVeterinarian());
        record.setNextDueDate(dto.getNextDueDate());
        record.setNotes(dto.getNotes());
    }
}
