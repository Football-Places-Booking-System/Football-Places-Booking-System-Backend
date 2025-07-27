package hypercell.final_project.football_places_booking_system.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Component("authService")
@RequiredArgsConstructor
public class AuthService {

    private final TeamMemberRepository teamMemberRepository;

    public boolean hasTeamRole(UUID teamId, String expectedRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) return false;

        User user = (User) authentication.getPrincipal();

        return teamMemberRepository.existsByUserIdAndTeamIdAndRole(user.getId(), teamId, expectedRole);
    }
}
