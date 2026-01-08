package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.BovineDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.service.BovineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(description = "Retorna todos os bovinos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna a lista de bovinos"),
            @ApiResponse(responseCode = "404", description = "Nenhum bovino encontrado")
    })
    @GetMapping
    public ResponseEntity<List<BovineDTO>> getAllBovines() {
        return ResponseEntity.ok(bovineService.getAllBovines());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o bovino encontrado"),
            @ApiResponse(responseCode = "404", description = "Bovino não encontrado")
    })
    @Operation(description = "Retorna um bovino pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<BovineDTO> getBovineById(@PathVariable Integer id) {
        return bovineService.getBovineById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(description = "Cria um novo bovino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o bovino criado"),
            @ApiResponse(responseCode = "400", description = "Erro de validação")
    })
    @PostMapping
    public ResponseEntity<BovineDTO> createBovine(@RequestBody BovineDTO dto) {
        return ResponseEntity.ok(bovineService.createBovine(dto));
    }

    @Operation(description = "Atualiza um bovino existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o bovino atualizado"),
            @ApiResponse(responseCode = "404", description = "Bovino não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BovineDTO> updateBovine(@PathVariable Integer id, @RequestBody BovineDTO dto) {
        return bovineService.updateBovine(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(description = "Exclui um bovino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bovino excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Bovino não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBovine(@PathVariable Integer id) {
        if (bovineService.deleteBovine(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
