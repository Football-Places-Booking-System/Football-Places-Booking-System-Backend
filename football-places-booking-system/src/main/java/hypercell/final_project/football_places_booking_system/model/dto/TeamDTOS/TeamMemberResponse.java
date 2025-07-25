package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import lombok.Builder;

@Builder
public record TeamMemberResponse(
        Long userId,
        String userName,
        TeamRole role,
        TeamStatus status
) { }
