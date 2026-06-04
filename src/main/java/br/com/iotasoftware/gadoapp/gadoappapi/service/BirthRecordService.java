package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BirthRecordDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.BirthRecord;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Bovine;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.BirthRecordRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.BovineRepository;
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
public class BirthRecordService {

    private final BirthRecordRepository birthRecordRepository;
    private final BovineRepository bovineRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void syncBirthRecords(List<BirthRecordDTO> dtos) {
        User user = getAuthenticatedUser();

        for (BirthRecordDTO dto : dtos) {
            if (dto.getId() != null) {
                Optional<BirthRecord> existingOpt = birthRecordRepository.findByIdAndMotherHerdUser(dto.getId(), user);
                if (existingOpt.isPresent()) {
                    BirthRecord existing = existingOpt.get();
                    updateFromDTO(existing, dto, user);
                    if (dto.getActive() != null) existing.setActive(dto.getActive());
                    birthRecordRepository.save(existing);
                } else {
                    createFromDTO(dto, user);
                }
            } else {
                createFromDTO(dto, user);
            }
        }
    }

    public List<BirthRecordDTO> getAllRecords() {
        return birthRecordRepository.findByMotherHerdUserAndActiveTrue(getAuthenticatedUser()).stream()
                .map(BirthRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<BirthRecordDTO> getAllRecords(Integer farmId) {
        return birthRecordRepository.findByMotherHerdFarmIdAndActiveTrue(farmId).stream()
                .map(BirthRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<BirthRecordDTO> getRecordsChangedSince(LocalDateTime since) {
        return birthRecordRepository.findByMotherHerdUserAndUpdatedAtAfter(getAuthenticatedUser(), since).stream()
                .map(BirthRecordDTO::new)
                .collect(Collectors.toList());
    }

    public List<BirthRecordDTO> getRecordsChangedSince(LocalDateTime since, Integer farmId) {
        return birthRecordRepository.findByMotherHerdFarmIdAndUpdatedAtAfter(farmId, since).stream()
                .map(BirthRecordDTO::new)
                .collect(Collectors.toList());
    }

    private BirthRecord createFromDTO(BirthRecordDTO dto, User user) {
        Bovine mother = bovineRepository.findByIdAndHerdUser(dto.getMotherId(), user)
                .orElseThrow(() -> new RuntimeException("Mãe não encontrada ou não pertence ao usuário"));

        BirthRecord record = BirthRecord.builder()
                .mother(mother)
                .calfId(dto.getCalfId())
                .birthDate(dto.getBirthDate())
                .notes(dto.getNotes())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
        return birthRecordRepository.save(record);
    }

    private void updateFromDTO(BirthRecord record, BirthRecordDTO dto, User user) {
        if (dto.getMotherId() != null) {
            Bovine mother = bovineRepository.findByIdAndHerdUser(dto.getMotherId(), user)
                    .orElseThrow(() -> new RuntimeException("Mãe não encontrada"));
            record.setMother(mother);
        }
        record.setCalfId(dto.getCalfId());
        record.setBirthDate(dto.getBirthDate());
        record.setNotes(dto.getNotes());
    }
}
