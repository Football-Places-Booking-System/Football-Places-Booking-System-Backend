package hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;

public class BookingMapper {

    private BookingMapper() {
        // Utility class - prevent instantiation
    }

    public static BookingResponseDTO toResponseDTO(BookingMatch match) {
        return BookingResponseDTO.builder()
                .id(match.getId())
                .startTime(match.getStartTime())
                .endTime(match.getEndTime())
                .status(match.getStatus())
                .userId(match.getUser().getId())
                .userName(match.getUser().getUserName())
                .teamId(match.getTeam().getId())
                .teamName(match.getTeam().getName())
                .placeId(match.getPlace().getId())
                .placeName(match.getPlace().getName())
                .build();
    }
}
