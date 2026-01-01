package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.Herd;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.HerdRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class HerdController {

    private final HerdRepository herdRepository;

    public HerdController(HerdRepository herdRepository) {
        this.herdRepository = herdRepository;
    }

    @GetMapping("/herds")
    public ResponseEntity<List<HerdDTO>> getAllHerds() {
        List<Herd> herds = herdRepository.findAll();
        return ResponseEntity.ok(herds.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    private HerdDTO convertToDTO(Herd herd) {
        return new HerdDTO(
                herd.getId(),
                herd.getName()
        );
    }
}