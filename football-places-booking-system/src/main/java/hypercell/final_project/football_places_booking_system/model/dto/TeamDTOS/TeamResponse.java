package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record TeamResponse(
        UUID id,
        String name,
        String description,
        UUID createdBy,
        String createdByUsername,
        LocalDateTime createdAt,
        List<TeamMemberResponse> members
) { }
