package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.db.TeamMember;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TeamResponse(
        Long id,
        String name,
        String description,
        Long createdBy,
        LocalDateTime createdAt,
        List<TeamMemberResponse> members
        //dto for returning teamMember
) { }
