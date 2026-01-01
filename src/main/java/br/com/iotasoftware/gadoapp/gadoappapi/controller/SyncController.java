package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BovineDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.SyncRequest;
import br.com.iotasoftware.gadoapp.gadoappapi.service.BovineService;
import br.com.iotasoftware.gadoapp.gadoappapi.service.HerdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final HerdService herdService;
    private final BovineService bovineService;

    public SyncController(HerdService herdService, BovineService bovineService) {
        this.herdService = herdService;
        this.bovineService = bovineService;
    }

    @PostMapping("/herds")
    public ResponseEntity<?> syncHerds(@RequestBody SyncRequest<HerdDTO> dto) {
        List<HerdDTO> herds = dto.getData();
        try {
            herdService.syncHerdsOverwriteSafely(herds);
            return ResponseEntity.ok(Map.of("message", "Rebanhos sincronizados com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }

    @PostMapping("/bovines")
    public ResponseEntity<?> syncBovines(@RequestBody SyncRequest<BovineDTO> dto) {
        List<BovineDTO> bovines = dto.getData();
        try {
            bovineService.syncBovinesOverwriteSafely(bovines);
            return ResponseEntity.ok(Map.of("message", "Bovinos sincronizados com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }
}
