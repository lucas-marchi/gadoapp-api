package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.HerdDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.service.HerdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/herd")
public class HerdController {

    private final HerdService herdService;

    public HerdController(HerdService herdService) {
        this.herdService = herdService;
    }

    @Operation(description = "Retorna todos os rebanhos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de rebanhos"),
            @ApiResponse(responseCode = "404", description = "Nenhum rebanho encontrado")
    })
    @GetMapping
    public ResponseEntity<List<HerdDTO>> getAllHerds() {
        return ResponseEntity.ok(herdService.getAllHerds());
    }

    @Operation(description = "Retorna um rebanho pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o rebanho encontrado"),
            @ApiResponse(responseCode = "404", description = "Rebanho não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HerdDTO> getHerdById(@PathVariable Integer id) {
        return herdService.getHerdById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(description = "Cria um novo rebanho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o rebanho criado"),
            @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    @PostMapping
    public ResponseEntity<HerdDTO> createHerd(@RequestBody HerdDTO dto) {
        return ResponseEntity.ok(herdService.createHerd(dto));
    }

    @Operation(description = "Atualiza um rebanho existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o rebanho atualizado"),
            @ApiResponse(responseCode = "404", description = "Rebanho não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HerdDTO> updateHerd(@PathVariable Integer id, @RequestBody HerdDTO dto) {
        return herdService.updateHerd(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(description = "Exclui um rebanho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rebanho excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rebanho não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHerd(@PathVariable Integer id) {
        if (herdService.deleteHerd(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
