package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeamDTO(
         UUID id,
         String name,
         String description,
         UUID creatorId,
         LocalDateTime createdAt
)
{ }