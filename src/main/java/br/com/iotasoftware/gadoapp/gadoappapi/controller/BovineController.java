package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BovineDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.service.BovineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bovines")
public class BovineController {

    private final BovineService bovineService;

    public BovineController(BovineService bovineService) {
        this.bovineService = bovineService;
    }

    @GetMapping
    public ResponseEntity<List<BovineDTO>> getAllBovines() {
        return ResponseEntity.ok(bovineService.getAllBovines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BovineDTO> getBovineById(@PathVariable Integer id) {
        return bovineService.getBovineById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BovineDTO> createBovine(@RequestBody BovineDTO dto) {
        return ResponseEntity.ok(bovineService.createBovine(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BovineDTO> updateBovine(@PathVariable Integer id, @RequestBody BovineDTO dto) {
        return bovineService.updateBovine(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBovine(@PathVariable Integer id) {
        if (bovineService.deleteBovine(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
