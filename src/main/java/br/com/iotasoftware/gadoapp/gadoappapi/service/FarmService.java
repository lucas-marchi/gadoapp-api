package br.com.iotasoftware.gadoapp.gadoappapi.service;

import br.com.iotasoftware.gadoapp.gadoappapi.dto.FarmDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.dto.FarmMemberDTO;
import br.com.iotasoftware.gadoapp.gadoappapi.model.*;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.FarmMemberRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.FarmRepository;
import br.com.iotasoftware.gadoapp.gadoappapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final FarmRepository farmRepository;
    private final FarmMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final SubscriptionLimitService limitService;

    @Transactional
    public FarmDTO createFarm(User owner, FarmDTO dto) {
        var limits = limitService.getLimitsForUser(owner);
        long currentFarms = memberRepository.findByUserIdAndStatus(owner.getId(), "ACTIVE")
                .stream().filter(m -> m.getRole() == FarmRole.OWNER).count();

        if (currentFarms >= limits.getMaxFarms()) {
            throw new br.com.iotasoftware.gadoapp.gadoappapi.exception.LimitExceededException("Limite de propriedades atingido para o seu plano atual.");
        }

        var farm = Farm.builder()
                .name(dto.getName())
                .inscricaoEstadual(dto.getInscricaoEstadual())
                .city(dto.getCity())
                .state(dto.getState())
                .address(dto.getAddress())
                .totalAreaHa(dto.getTotalAreaHa())
                .active(true)
                .build();

        farm = farmRepository.save(farm);

        var member = FarmMember.builder()
                .user(owner)
                .farm(farm)
                .role(FarmRole.OWNER)
                .status("ACTIVE")
                .build();

        memberRepository.save(member);

        return toDTO(farm, "OWNER", 0, 0, 1);
    }

    @Transactional
    public FarmDTO updateFarm(Integer farmId, FarmDTO dto, User user) {
        var farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        assertPermission(user.getId(), farmId, FarmRole.ADMIN);

        farm.setName(dto.getName());
        farm.setInscricaoEstadual(dto.getInscricaoEstadual());
        farm.setCity(dto.getCity());
        farm.setState(dto.getState());
        farm.setAddress(dto.getAddress());
        farm.setTotalAreaHa(dto.getTotalAreaHa());

        farm = farmRepository.save(farm);

        var membership = memberRepository.findByUserIdAndFarmId(user.getId(), farmId).orElse(null);
        String role = membership != null ? membership.getRole().name() : "VIEWER";
        int herdCount = (int) farm.getHerds().stream().filter(Herd::getActive).count();

        return toDTO(farm, role, herdCount, 0, farm.getMembers().size());
    }

    public List<FarmDTO> listFarmsForUser(Integer userId) {
        var memberships = memberRepository.findByUserIdAndStatus(userId, "ACTIVE");

        return memberships.stream().map(m -> {
            var farm = m.getFarm();
            int herdCount = (int) farm.getHerds().stream().filter(Herd::getActive).count();
            int bovineCount = farm.getHerds().stream()
                    .filter(Herd::getActive)
                    .mapToInt(h -> (int) h.getBovines().stream().filter(Bovine::getActive).count())
                    .sum();
            int memberCount = (int) farm.getMembers().stream()
                    .filter(fm -> "ACTIVE".equals(fm.getStatus()))
                    .count();
            return toDTO(farm, m.getRole().name(), herdCount, bovineCount, memberCount);
        }).toList();
    }

    @Transactional
    public FarmMemberDTO inviteMember(Integer farmId, String email, String role, User inviter) {
        assertPermission(inviter.getId(), farmId, FarmRole.ADMIN);

        var farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        var invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        var existing = memberRepository.findByUserIdAndFarmId(invitedUser.getId(), farmId);
        if (existing.isPresent()) {
            throw new RuntimeException("User is already a member of this farm");
        }

        var limits = limitService.getLimitsForUser(inviter);
        long currentInvites = memberRepository.findByFarmIdAndStatus(farmId, "ACTIVE").stream().filter(m -> m.getRole() != FarmRole.OWNER).count()
                + memberRepository.findByFarmIdAndStatus(farmId, "PENDING").size();
        if (currentInvites >= limits.getMaxInvitesPerFarm()) {
            throw new br.com.iotasoftware.gadoapp.gadoappapi.exception.LimitExceededException("Limite de membros/convites atingido para esta propriedade no plano atual.");
        }

        var member = FarmMember.builder()
                .user(invitedUser)
                .farm(farm)
                .role(FarmRole.valueOf(role))
                .status("PENDING")
                .build();

        member = memberRepository.save(member);

        return toMemberDTO(member);
    }

    @Transactional
    public void acceptInvite(Integer memberId, User user) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));

        if (!member.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This invite is not for you");
        }

        member.setStatus("ACTIVE");
        memberRepository.save(member);
    }

    @Transactional
    public void declineInvite(Integer memberId, User user) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Invite not found"));

        if (!member.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This invite is not for you");
        }

        memberRepository.delete(member);
    }

    @Transactional
    public void removeMember(Integer farmId, Integer memberId, User requester) {
        assertPermission(requester.getId(), farmId, FarmRole.ADMIN);

        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getRole() == FarmRole.OWNER) {
            throw new RuntimeException("Cannot remove the farm owner");
        }

        memberRepository.delete(member);
    }

    public List<FarmMemberDTO> listMembers(Integer farmId, User user) {
        assertPermission(user.getId(), farmId, FarmRole.VIEWER);

        return memberRepository.findByFarmIdAndStatus(farmId, "ACTIVE")
                .stream()
                .map(this::toMemberDTO)
                .toList();
    }

    public List<FarmMemberDTO> listPendingInvites(User user) {
        return memberRepository.findByUserIdAndStatus(user.getId(), "PENDING")
                .stream()
                .map(m -> FarmMemberDTO.builder()
                        .id(m.getId())
                        .userId(m.getUser().getId())
                        .userName(m.getFarm().getName())
                        .userEmail(m.getRole().name())
                        .role(m.getRole().name())
                        .status(m.getStatus())
                        .build())
                .toList();
    }

    public Farm createDefaultFarm(User user) {
        var farm = Farm.builder()
                .name(user.getName() + " - Propriedade")
                .active(true)
                .build();
        farm = farmRepository.save(farm);

        var member = FarmMember.builder()
                .user(user)
                .farm(farm)
                .role(FarmRole.OWNER)
                .status("ACTIVE")
                .build();
        memberRepository.save(member);

        return farm;
    }

    // === Helpers ===

    private void assertPermission(Integer userId, Integer farmId, FarmRole minimumRole) {
        var membership = memberRepository.findByUserIdAndFarmId(userId, farmId)
                .orElseThrow(() -> new RuntimeException("Not a member of this farm"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new RuntimeException("Membership is not active");
        }

        if (minimumRole == FarmRole.ADMIN && membership.getRole() == FarmRole.VIEWER) {
            throw new RuntimeException("Insufficient permissions");
        }

        if (minimumRole == FarmRole.OWNER && membership.getRole() != FarmRole.OWNER) {
            throw new RuntimeException("Only the owner can do this");
        }
    }

    private FarmDTO toDTO(Farm farm, String role, int herdCount, int bovineCount, int memberCount) {
        return FarmDTO.builder()
                .id(farm.getId())
                .name(farm.getName())
                .inscricaoEstadual(farm.getInscricaoEstadual())
                .city(farm.getCity())
                .state(farm.getState())
                .address(farm.getAddress())
                .totalAreaHa(farm.getTotalAreaHa())
                .role(role)
                .herdCount(herdCount)
                .bovineCount(bovineCount)
                .memberCount(memberCount)
                .build();
    }

    private FarmMemberDTO toMemberDTO(FarmMember m) {
        return FarmMemberDTO.builder()
                .id(m.getId())
                .userId(m.getUser().getId())
                .userName(m.getUser().getName())
                .userEmail(m.getUser().getEmail())
                .role(m.getRole().name())
                .status(m.getStatus())
                .build();
    }
}
