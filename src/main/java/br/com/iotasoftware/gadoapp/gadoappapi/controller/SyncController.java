package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.*;
import br.com.iotasoftware.gadoapp.gadoappapi.service.BirthRecordService;
import br.com.iotasoftware.gadoapp.gadoappapi.service.BovineService;
import br.com.iotasoftware.gadoapp.gadoappapi.service.HealthRecordService;
import br.com.iotasoftware.gadoapp.gadoappapi.service.HerdService;
import br.com.iotasoftware.gadoapp.gadoappapi.service.WeightRecordService;
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
    private final WeightRecordService weightRecordService;
    private final BirthRecordService birthRecordService;
    private final HealthRecordService healthRecordService;

    public SyncController(HerdService herdService, BovineService bovineService,
                          WeightRecordService weightRecordService, BirthRecordService birthRecordService,
                          HealthRecordService healthRecordService) {
        this.herdService = herdService;
        this.bovineService = bovineService;
        this.weightRecordService = weightRecordService;
        this.birthRecordService = birthRecordService;
        this.healthRecordService = healthRecordService;
    }

    // ========== HERDS ==========

    @Operation(description = "Sincroniza os rebanhos, cliente envia mudanças para o Servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rebanhos sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na sincronização"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Nenhum rebanho encontrado")
    })
    @PostMapping("/herds/push")
    public ResponseEntity<?> pushHerds(
            @RequestBody SyncRequest<HerdDTO> dto,
            @RequestParam(required = false) Integer farmId) {
        List<HerdDTO> herds = dto.getData();
        try {
            if (farmId != null) {
                herdService.syncHerds(herds, farmId);
            } else {
                herdService.syncHerds(herds);
            }
            return ResponseEntity.ok(Map.of("message", "Rebanhos sincronizados com sucesso"));
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            @RequestParam(required = false) Integer farmId) {
        if (farmId != null) {
            if (since == null) {
                return ResponseEntity.ok(herdService.getAllHerds(farmId));
            }
            return ResponseEntity.ok(herdService.getHerdsChangedSince(since, farmId));
        }
        // Legacy fallback
        if (since == null) {
            return ResponseEntity.ok(herdService.getAllHerds());
        }
        return ResponseEntity.ok(herdService.getHerdsChangedSince(since));
    }

    // ========== BOVINES ==========

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

    // ========== WEIGHT RECORDS ==========

    @Operation(description = "Sincroniza registros de peso, cliente envia mudanças para o Servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros de peso sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na sincronização")
    })
    @PostMapping("/weight-records/push")
    public ResponseEntity<?> pushWeightRecords(@RequestBody SyncRequest<WeightRecordDTO> dto) {
        List<WeightRecordDTO> records = dto.getData();
        try {
            weightRecordService.syncWeightRecords(records);
            return ResponseEntity.ok(Map.of("message", "Registros de peso sincronizados com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }

    @Operation(description = "Cliente pede registros de peso desde a última sincronização")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de registros de peso")
    })
    @GetMapping("/weight-records/pull")
    public ResponseEntity<List<WeightRecordDTO>> pullWeightRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        if (since == null) {
            return ResponseEntity.ok(weightRecordService.getAllRecords());
        }
        return ResponseEntity.ok(weightRecordService.getRecordsChangedSince(since));
    }

    // ========== BIRTH RECORDS ==========

    @Operation(description = "Sincroniza registros de nascimento, cliente envia mudanças para o Servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros de nascimento sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na sincronização")
    })
    @PostMapping("/birth-records/push")
    public ResponseEntity<?> pushBirthRecords(@RequestBody SyncRequest<BirthRecordDTO> dto) {
        List<BirthRecordDTO> records = dto.getData();
        try {
            birthRecordService.syncBirthRecords(records);
            return ResponseEntity.ok(Map.of("message", "Registros de nascimento sincronizados com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }

    @Operation(description = "Cliente pede registros de nascimento desde a última sincronização")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de registros de nascimento")
    })
    @GetMapping("/birth-records/pull")
    public ResponseEntity<List<BirthRecordDTO>> pullBirthRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        if (since == null) {
            return ResponseEntity.ok(birthRecordService.getAllRecords());
        }
        return ResponseEntity.ok(birthRecordService.getRecordsChangedSince(since));
    }

    // ========== HEALTH RECORDS ==========

    @Operation(description = "Sincroniza registros de saúde, cliente envia mudanças para o Servidor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros de saúde sincronizados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro na sincronização")
    })
    @PostMapping("/health-records/push")
    public ResponseEntity<?> pushHealthRecords(@RequestBody SyncRequest<HealthRecordDTO> dto) {
        List<HealthRecordDTO> records = dto.getData();
        try {
            healthRecordService.syncHealthRecords(records);
            return ResponseEntity.ok(Map.of("message", "Registros de saúde sincronizados com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro na sincronização: " + e.getMessage())
            );
        }
    }

    @Operation(description = "Cliente pede registros de saúde desde a última sincronização")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de registros de saúde")
    })
    @GetMapping("/health-records/pull")
    public ResponseEntity<List<HealthRecordDTO>> pullHealthRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        if (since == null) {
            return ResponseEntity.ok(healthRecordService.getAllRecords());
        }
        return ResponseEntity.ok(healthRecordService.getRecordsChangedSince(since));
    }
}
