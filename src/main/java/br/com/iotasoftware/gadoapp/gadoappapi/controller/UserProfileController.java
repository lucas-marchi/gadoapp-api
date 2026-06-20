package br.com.iotasoftware.gadoapp.gadoappapi.controller;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.ChangePasswordRequest;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.UserProfileDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.User;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final br.com.iotasoftware.gadoapp.gadoappapi.service.SubscriptionLimitService limitService;
    private final br.com.iotasoftware.gadoapp.gadoappapi.repository.FarmMemberRepository farmMemberRepository;

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(buildDto(user));
    }

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserProfileDTO dto
    ) {
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        userRepository.save(user);

        return ResponseEntity.ok(buildDto(user));
    }

    private UserProfileDTO buildDto(User user) {
        var limits = limitService.getLimitsForUser(user);
        int totalFarms = (int) farmMemberRepository.findByUserIdAndStatus(user.getId(), "ACTIVE")
                .stream()
                .filter(m -> m.getRole() == br.com.iotasoftware.gadoapp.gadoappapi.model.FarmRole.OWNER)
                .count();

        return UserProfileDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .subscriptionStatus(user.getSubscriptionStatus())
                .stripePriceId(user.getStripePriceId())
                .limits(limits)
                .usage(new br.com.iotasoftware.gadoapp.gadoappapi.dto.SubscriptionUsage(totalFarms))
                .build();
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @jakarta.validation.Valid @RequestBody ChangePasswordRequest request
    ) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Senha atual incorreta"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
    }
}
