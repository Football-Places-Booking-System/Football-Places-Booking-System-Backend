package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import lombok.Builder;

@Builder
public record TeamMemberResponse(
        UUID id,
        UUID userId,
        String userName,
        String email,
        TeamRole role,
        TeamStatus status,
        UUID teamId
) { }
