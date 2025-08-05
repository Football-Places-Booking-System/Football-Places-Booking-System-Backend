package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;
import hypercell.final_project.football_places_booking_system.service.Interfaces.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TeamMemberRepository teamMemberRepository;

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }

    @Override
    public boolean hasTeamRole(UUID teamId, String expectedRole) throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || 
            !authentication.isAuthenticated() ||
            authentication.getPrincipal() == "anonymousUser") return false;

        User user = (User) authentication.getPrincipal();

        TeamRole role = TeamRole.valueOf(expectedRole);

        getCurrentRequest().setAttribute("ERROR", "NOT_ORGANIZER");
        
        return (teamMemberRepository.existsByUserIdAndTeamIdAndRole(user.getId(), teamId, role));
    }

    @Override
    public boolean is(String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || 
            !authentication.isAuthenticated() ||
            authentication.getPrincipal() == "anonymousUser") return false;
            
        User user = (User) authentication.getPrincipal();

        getCurrentRequest().setAttribute("ERROR", "NOT_ACTIVE");

        return (user.getStatus() == UserStatus.valueOf(status));
    }
}
