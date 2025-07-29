package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import lombok.Builder;
import java.util.UUID;

@Builder
public record TeamMemberResponse(
        UUID userId,
        String userName,
        TeamRole role,
        TeamStatus status,
        UUID teamId
) { }
