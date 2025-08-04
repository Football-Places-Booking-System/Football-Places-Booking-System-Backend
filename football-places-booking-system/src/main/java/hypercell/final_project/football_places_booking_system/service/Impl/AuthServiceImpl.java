package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TeamMemberRepository teamMemberRepository;

    public boolean hasTeamRole(UUID teamId, String expectedRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || 
            !authentication.isAuthenticated() ||
            authentication.getPrincipal() == "anonymousUser") return false;

        User user = (User) authentication.getPrincipal();

        TeamRole role;
        try {
            role = TeamRole.valueOf(expectedRole);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return teamMemberRepository.existsByUserIdAndTeamIdAndRole(user.getId(), teamId, role);
    }
}
