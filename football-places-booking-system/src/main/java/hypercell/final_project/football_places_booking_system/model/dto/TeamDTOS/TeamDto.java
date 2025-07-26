package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import java.time.LocalDateTime;

public record TeamDto(
         Long id,
         String name,
         String description,
         Long creatorId,
         LocalDateTime createdAt
)
{ }