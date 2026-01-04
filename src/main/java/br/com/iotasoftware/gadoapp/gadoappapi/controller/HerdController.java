package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.service.HerdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/herds")
public class HerdController {

    private final HerdService herdService;

    public HerdController(HerdService herdService) {
        this.herdService = herdService;
    }

    @GetMapping
    public ResponseEntity<List<HerdDTO>> getAllHerds() {
        return ResponseEntity.ok(herdService.getAllHerds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HerdDTO> getHerdById(@PathVariable Integer id) {
        return herdService.getHerdById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HerdDTO> createHerd(@RequestBody HerdDTO dto) {
        return ResponseEntity.ok(herdService.createHerd(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HerdDTO> updateHerd(@PathVariable Integer id, @RequestBody HerdDTO dto) {
        return herdService.updateHerd(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHerd(@PathVariable Integer id) {
        if (herdService.deleteHerd(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
