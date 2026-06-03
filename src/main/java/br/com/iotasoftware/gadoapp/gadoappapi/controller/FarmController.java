package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.FarmDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.FarmMemberDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.InviteRequest;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    @GetMapping
    public ResponseEntity<List<FarmDTO>> listFarms(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(farmService.listFarmsForUser(user.getId()));
    }

    @PostMapping
    public ResponseEntity<FarmDTO> createFarm(
            @AuthenticationPrincipal User user,
            @RequestBody FarmDTO dto
    ) {
        return ResponseEntity.ok(farmService.createFarm(user, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmDTO> updateFarm(
            @PathVariable Integer id,
            @RequestBody FarmDTO dto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(farmService.updateFarm(id, dto, user));
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<FarmMemberDTO> inviteMember(
            @PathVariable Integer id,
            @RequestBody InviteRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(farmService.inviteMember(id, request.getEmail(), request.getRole(), user));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<FarmMemberDTO>> listMembers(
            @PathVariable Integer id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(farmService.listMembers(id, user));
    }

    @DeleteMapping("/{farmId}/members/{memberId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @PathVariable Integer farmId,
            @PathVariable Integer memberId,
            @AuthenticationPrincipal User user
    ) {
        farmService.removeMember(farmId, memberId, user);
        return ResponseEntity.ok(Map.of("message", "Member removed"));
    }

    @GetMapping("/invites/pending")
    public ResponseEntity<List<FarmMemberDTO>> pendingInvites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(farmService.listPendingInvites(user));
    }

    @PostMapping("/invites/{id}/accept")
    public ResponseEntity<Map<String, String>> acceptInvite(
            @PathVariable Integer id,
            @AuthenticationPrincipal User user
    ) {
        farmService.acceptInvite(id, user);
        return ResponseEntity.ok(Map.of("message", "Invite accepted"));
    }

    @PostMapping("/invites/{id}/decline")
    public ResponseEntity<Map<String, String>> declineInvite(
            @PathVariable Integer id,
            @AuthenticationPrincipal User user
    ) {
        farmService.declineInvite(id, user);
        return ResponseEntity.ok(Map.of("message", "Invite declined"));
    }
}
