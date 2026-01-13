package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BovineDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.SyncRequest;
import br.com.iotasoftware.gadoapp.gadoappapi.service.BovineService;
import br.com.iotasoftware.gadoapp.gadoappapi.service.HerdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Operation(description = "Sincroniza os rebanhos, cliente envia mudanças para o Servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rebanhos sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na sincronização"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Nenhum rebanho encontrado")
    })
    @PostMapping("/herds/push")
    public ResponseEntity<?> pushHerds(@RequestBody SyncRequest<HerdDTO> dto) {
        List<HerdDTO> herds = dto.getData();
        try {
            herdService.syncHerds(herds);
            return ResponseEntity.ok(Map.of("message", "Rebanhos sincronizados com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }

    @Operation(description = "Sincroniza os bovinos, cliente envia mudanças para o Servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bovinos sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na sincronização"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Nenhum bovino encontrado")
    })
    @PostMapping("/bovines/push")
    public ResponseEntity<?> pushBovines(@RequestBody SyncRequest<BovineDTO> dto) {
        List<BovineDTO> bovines = dto.getData();
        try {
            bovineService.syncBovines(bovines);
            return ResponseEntity.ok(Map.of("message", "Bovinos sincronizados com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }

    @Operation(description = "Cliente pede o que mudou desde a última vez da lista de rebanhos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de rebanhos"),
            @ApiResponse(responseCode = "404", description = "Nenhum rebanho encontrado")
    })
    @GetMapping("/herds/pull")
    public ResponseEntity<List<HerdDTO>> pullHerds(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        if (since == null) {
            return ResponseEntity.ok(herdService.getAllHerds());
        }
        return ResponseEntity.ok(herdService.getHerdsChangedSince(since));
    }

    @Operation(description = "Cliente pede o que mudou desde a última vez da lista de bovinos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de bovinos"),
            @ApiResponse(responseCode = "404", description = "Nenhum bovino encontrado")
    })
    @GetMapping("/bovines/pull")
    public ResponseEntity<List<BovineDTO>> pullBovines(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        if (since == null) {
            return ResponseEntity.ok(bovineService.getAllBovines());
        }
        return ResponseEntity.ok(bovineService.getBovinesChangedSince(since));
    }
}
