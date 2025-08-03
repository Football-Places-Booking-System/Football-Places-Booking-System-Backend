package hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs;

import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;

public class MatchPartMapper {

    public static MatchPartResponseDTO toResponseDTO(MatchParticipant participant) {
        return MatchPartResponseDTO.builder()
                .id(participant.getId())
                .status(participant.getStatus())
                .bookingMatchId(participant.getBookingMatch().getId())
                .userId(participant.getUser().getId())
                .userEmail(participant.getUser().getEmail())
                .username(participant.getUser().getUserName())
                .build();
    }
}
