package hypercell.final_project.football_places_booking_system.model.dto;

import java.time.LocalDateTime;

public record BookingMatchDTO(
        Long placeId,
        Long userId,
        Long teamId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}

